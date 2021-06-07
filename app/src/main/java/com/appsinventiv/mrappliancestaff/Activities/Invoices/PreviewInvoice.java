package com.appsinventiv.mrappliancestaff.Activities.Invoices;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.mrappliancestaff.Adapters.PreviewInvoiceAdapter;
import com.appsinventiv.mrappliancestaff.Models.CustomInvoiceModel;
import com.appsinventiv.mrappliancestaff.Models.InvoiceItemModel;
import com.appsinventiv.mrappliancestaff.Models.PaymentModel;
import com.appsinventiv.mrappliancestaff.R;
import com.appsinventiv.mrappliancestaff.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class PreviewInvoice extends Fragment {

    RecyclerView recycler;
    private View rootView;
    PreviewInvoiceAdapter adapter;
    private ArrayList<InvoiceItemModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;
    private CustomInvoiceModel customInvoiceModel;
    TextView total;
    TextView billTo, invoiceId;
    TextView paid, balance;
    RelativeLayout invoiceLayout;
    ImageView sendInvoice;
    TextView companyDetails;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_preview_invoice, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recycler = rootView.findViewById(R.id.recycler);
        invoiceLayout = rootView.findViewById(R.id.invoiceLayout);
        invoiceId = rootView.findViewById(R.id.invoiceId);
        sendInvoice = rootView.findViewById(R.id.sendInvoice);
        companyDetails = rootView.findViewById(R.id.companyDetails);
        billTo = rootView.findViewById(R.id.billTo);
        balance = rootView.findViewById(R.id.balance);
        paid = rootView.findViewById(R.id.paid);
        total = rootView.findViewById(R.id.total);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new PreviewInvoiceAdapter(getContext(), itemList);
        recycler.setAdapter(adapter);
        sendInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewToBitmap(invoiceLayout, invoiceLayout.getWidth(), invoiceLayout.getHeight());
            }
        });
        getAddressFromDb();
        return rootView;
    }

    private void getAddressFromDb() {
        mDatabase.child("Admin").child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String addd = dataSnapshot.getValue(String.class);
                    companyDetails.setText("From: \n" + addd);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public Bitmap viewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
//        bitmap = view.getDrawingCache();

        Uri ui = getImageUri(bitmap);
        CommonUtils.showToast("Saved Image");
//        uploadPicture(CommonUtils.getRealPathFromURI(ui));
        return bitmap;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            getDataFromServer();
        } else {

        }
    }

    private void getDataFromServer() {
        mDatabase.child("Invoices").child(InvoicesActivity.invoiceId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    customInvoiceModel = dataSnapshot.getValue(CustomInvoiceModel.class);
                    if (customInvoiceModel != null) {
                        itemList = new ArrayList<>(customInvoiceModel.getInvoiceItems().values());
                        adapter.setItemList(itemList);
                        invoiceId.setText("Invoice: " + customInvoiceModel.getInvoiceId() + "\nDate: " + CommonUtils.getDateOnly(customInvoiceModel.getTime()));
                        total.setText("AED " + customInvoiceModel.getTotal());
                        billTo.setText("To: \n " + customInvoiceModel.getUser().getFirstname() + " " + customInvoiceModel.getUser().getLastname() + "\n" +
                                customInvoiceModel.getUser().getAddress() + "\n" + customInvoiceModel.getUser().getPhone() + "\n" + customInvoiceModel.getUser().getEmail());
                        calculateTotal();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void calculateTotal() {
        int totall = 0;
        for (InvoiceItemModel model : itemList) {
            totall = totall + model.getQuantity() * model.getPrice();
        }
        total.setText("AED " + totall);

        int totalPaid = 0;
        if (customInvoiceModel != null && customInvoiceModel.getPayments() != null) {

            ArrayList<PaymentModel> listt = new ArrayList<>(customInvoiceModel.getPayments().values());
            for (PaymentModel model : listt) {
                totalPaid = totalPaid + model.getPrice();
            }
            paid.setText("AED " + totalPaid);
            balance.setText("AED " + (customInvoiceModel.getTotal() - totalPaid));
        }
    }

}
