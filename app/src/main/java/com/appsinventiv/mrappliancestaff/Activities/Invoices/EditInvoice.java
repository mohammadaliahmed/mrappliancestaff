package com.appsinventiv.mrappliancestaff.Activities.Invoices;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Activities.Jobs.NewAppointment;
import com.appsinventiv.mrappliancestaff.Activities.PaymentsList;
import com.appsinventiv.mrappliancestaff.Adapters.InvoiceItemAdapter;
import com.appsinventiv.mrappliancestaff.Models.CustomInvoiceModel;
import com.appsinventiv.mrappliancestaff.Models.InvoiceItemModel;
import com.appsinventiv.mrappliancestaff.Models.PaymentModel;
import com.appsinventiv.mrappliancestaff.Models.ServiceModel;
import com.appsinventiv.mrappliancestaff.Models.SubServiceModel;
import com.appsinventiv.mrappliancestaff.Models.User;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;
import com.appsinventiv.mrappliancestaff.Utils.SharedPrefs;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EditInvoice extends Fragment {
    private View rootView;
    DatabaseReference mDatabase;
    Spinner spinner;
    Button addItem;
    String invoiceId = InvoicesActivity.invoiceId;
    Button saveInvoice;
    ArrayList<InvoiceItemModel> itemList = new ArrayList<>();
    ArrayList<User> userList = new ArrayList<>();
    RecyclerView itemsRecycler;
    InvoiceItemAdapter adapter;
    private CustomInvoiceModel invoiceModel;
    private HashMap<String, InvoiceItemModel> invoiceItemMap = new HashMap<>();
    private int total = 0;

    TextView totalValue, subtotal, invoiceStatus;
    private User selectedUser;
    TextView invoiceIdTv, date;
    TextView userChosen;

    RelativeLayout payments;
    TextView markAsPaid;
    TextView paid, balance, paidDate;
    Button createNewCustomer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_invoice, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        paid = rootView.findViewById(R.id.paid);
        paidDate = rootView.findViewById(R.id.paidDate);
        balance = rootView.findViewById(R.id.balance);
        totalValue = rootView.findViewById(R.id.totalValue);
        subtotal = rootView.findViewById(R.id.subtotal);
        payments = rootView.findViewById(R.id.payments);
        invoiceStatus = rootView.findViewById(R.id.invoiceStatus);
        markAsPaid = rootView.findViewById(R.id.markAsPaid);
        itemsRecycler = rootView.findViewById(R.id.itemsRecycler);
        date = rootView.findViewById(R.id.date);
        createNewCustomer = rootView.findViewById(R.id.createNewCustomer);
        saveInvoice = rootView.findViewById(R.id.saveInvoice);
        userChosen = rootView.findViewById(R.id.userChosen);
        invoiceIdTv = rootView.findViewById(R.id.invoiceId);
        addItem = rootView.findViewById(R.id.addItem);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new InvoiceItemAdapter(getContext(), itemList, new InvoiceItemAdapter.InvoiceItemAdapterCallback() {
            @Override
            public void onDelete(InvoiceItemModel model) {
                showDleteAlert(model);
            }

        });
        if (NewAppointment.newAppointmentUsr != null) {
            selectedUser = NewAppointment.newAppointmentUsr;
            userChosen.setVisibility(View.VISIBLE);
            userChosen.setText("User: " + selectedUser.getFullName());
        }
        createNewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateCustomerAlert();
            }
        });
        itemsRecycler.setAdapter(adapter);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNow();
            }
        });
        invoiceIdTv.setText("Invoice: " + invoiceId);

        saveInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotal();
                CustomInvoiceModel model = new CustomInvoiceModel(invoiceId, invoiceItemMap, total,
                        System.currentTimeMillis(), "pending", selectedUser, SharedPrefs.getUser().getUsername(), NewAppointment.id,"");
                HashMap<String, Object> map = new HashMap<>();
                if (invoiceModel != null) {
                    model.setPayments(invoiceModel.getPayments());
                } else {
                    model.setPayments(new HashMap<>());
                }

                map.put(invoiceId, model);
                mDatabase.child("Invoices").updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (NewAppointment.id != null) {
                            mDatabase.child("Appointments").child(NewAppointment.id).child("invoiceId").setValue("" + invoiceId);
                        }
                        CommonUtils.showToast("Saved");
                    }
                });
            }
        });
        payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PaymentsList.class);
                i.putExtra("invoiceId", invoiceId);
                startActivity(i);
            }
        });
        getListOfUsers();
        markAsPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Invoices").child(invoiceId).child("status").setValue("paid").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Marked as paid");
                    }
                });
            }
        });

        return rootView;
    }

    private void showCreateCustomerAlert() {
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(R.layout.alert_dialog_add_customer, null);

        dialog.setContentView(layout);

        EditText name = layout.findViewById(R.id.name);
        EditText phone = layout.findViewById(R.id.phone);
        EditText address = layout.findViewById(R.id.address);
        Button save = layout.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() < 5) {
                    name.setError("Enter full Name");
                } else if (phone.getText().length() < 5) {
                    phone.setError("Enter phone");
                } else if (address.getText().length() < 5) {
                    address.setError("Enter address");
                } else {

                    String[] fullname = name.getText().toString().split(" ");
                    try {

                        String abc = fullname[1];
                        selectedUser = new User(

                                name.getText().toString().split(" ")[0],
                                name.getText().toString().split(" ")[1],
                                (name.getText().toString().replace(" ", "")).toLowerCase(),
                                (name.getText().toString().replace(" ", "")).toLowerCase(),
                                name.getText().toString().replace(" ", "") + "@gmail.com",
                                phone.getText().toString(),
                                phone.getText().toString(),
                                address.getText().toString(),
                                "",
                                System.currentTimeMillis(),
                                true
                        );
                        mDatabase.child("Users").child(selectedUser.getUsername()).setValue(selectedUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                CommonUtils.showToast("User Added");
                                dialog.dismiss();
                                userChosen.setVisibility(View.VISIBLE);
                                userChosen.setText("User: " + selectedUser.getFullName());
                            }
                        });
                    } catch (Exception e) {
                        CommonUtils.showToast("Please enter full name");
                        name.requestFocus();
                        name.setError("Enter full name");
                    }


                }
            }
        });


        dialog.show();
    }

    private void showDleteAlert(final InvoiceItemModel model) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alert");
        builder.setMessage("Do you want to delete this item? ");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Invoices").child(invoiceId).child("invoiceItems").child(model.getItemId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Removed");
                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialogNow() {
        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.alert_dialog_add_item, null);
        dialog.setContentView(layout);

        final EditText description = layout.findViewById(R.id.description);
        final EditText cost = layout.findViewById(R.id.cost);
        final EditText quantity = layout.findViewById(R.id.quantity);
        final TextView total = layout.findViewById(R.id.total);
        final Button saveItem = layout.findViewById(R.id.saveItem);
        final int[] totalPrice = {0};
        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && cost.getText().length() > 0) {
                    totalPrice[0] = Integer.parseInt(cost.getText().toString()) * Integer.parseInt(quantity.getText().toString());
                    total.setText("Rs " + totalPrice[0]);
                }
            }
        });
        cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && quantity.getText().length() > 0) {
                    totalPrice[0] = Integer.parseInt(cost.getText().toString()) * Integer.parseInt(quantity.getText().toString());
                    total.setText("Rs " + totalPrice[0]);
                }
            }
        });
        saveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (description.getText().length() < 1) {
                    description.setError("Enter description");
                } else if (quantity.getText().length() == 0) {
                    quantity.setError("Enter quantity");
                } else if (cost.getText().length() == 0) {
                    cost.setError("Enter cost");
                } else {
                    String key = mDatabase.push().getKey();
                    InvoiceItemModel invoiceItemModel = new InvoiceItemModel(key, description.getText().toString(),
                            Integer.parseInt(quantity.getText().toString()), Integer.parseInt(cost.getText().toString()));
                    invoiceItemMap.put(key, invoiceItemModel);
                    itemList = new ArrayList<>(invoiceItemMap.values());
                    calculateTotal();
                    adapter.setItemList(itemList);

                }
                dialog.cancel();
            }
        });


        dialog.show();
    }

    private void getListOfUsers() {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.getFirstname() != null) {
                            userList.add(user);
                        }
                    }
                    setupSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setupSpinner() {

        final ArrayList<String> items = new ArrayList<>();

        items.add("Select Customer");
        for (User user : userList) {
            items.add(user.getFirstname() + "(" + user.getPhone() + ")");
        }


        spinner = rootView.findViewById(R.id.spinner);
        final ArrayAdapter<String> adaptera = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adaptera);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {

                    selectedUser = userList.get(i - 1);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (invoiceId != null) {
                getInvoiceDataFromServer();
            }
        } else {

        }
    }

    private void getInvoiceDataFromServer() {
        invoiceItemMap.clear();
        itemList.clear();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Invoices").child(invoiceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    invoiceItemMap.clear();
                    invoiceModel = dataSnapshot.getValue(CustomInvoiceModel.class);
                    if (invoiceModel != null) {
                        setupData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupData() {
        if (invoiceModel.getStatus().equalsIgnoreCase("paid")) {
            markAsPaid.setVisibility(View.GONE);
        }
        selectedUser = invoiceModel.getUser();
        invoiceStatus.setText(invoiceModel.getStatus());
        userChosen.setVisibility(View.VISIBLE);
        userChosen.setText(invoiceModel.getUser().getFirstname() + " (" + invoiceModel.getUser().getPhone() + ")");
        date.setText(CommonUtils.getFormattedDate(invoiceModel.getTime()));
        invoiceItemMap = invoiceModel.getInvoiceItems();
        itemList = new ArrayList<>(invoiceItemMap.values());
        adapter.setItemList(itemList);
        calculateTotal();

    }

    private void calculateTotal() {
        total = 0;
        for (InvoiceItemModel model : itemList) {
            total = total + model.getQuantity() * model.getPrice();
        }
        totalValue.setText("AED " + total);
        subtotal.setText("AED " + total);

        int totalPaid = 0;
        if (invoiceModel != null && invoiceModel.getPayments() != null) {
            ArrayList<PaymentModel> listt = new ArrayList<>(invoiceModel.getPayments().values());
            for (PaymentModel model : listt) {
                totalPaid = totalPaid + model.getPrice();
            }
            paid.setText("AED " + totalPaid);
            balance.setText("AED " + (invoiceModel.getTotal() - totalPaid));
        }
    }
}
