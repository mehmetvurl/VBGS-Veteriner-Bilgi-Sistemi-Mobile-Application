package com.vuralsoftware.vbgs.fragments.hayvansorgula;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vuralsoftware.vbgs.activity.BottomView;
import com.vuralsoftware.vbgs.R;

public class Hayvansorgula extends Fragment {

    private HayvansorgulaViewModel mViewModel;
    private BottomView nvb;
    private EditText txt;
    private String tasima;
    private FirebaseUser mUser;
    private FirebaseFirestore mFireStore;
    private DocumentReference docref,docref2;
    private View genel;
    private IntentResult result;

    public static Hayvansorgula newInstance() {
        return new Hayvansorgula();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        genel= inflater.inflate(R.layout.fragment_hayvansorgula, container, false);
        txt = genel.findViewById(R.id.hayvansorgula_txt);
        nvb=new BottomView();
        ImageButton btn_search = genel.findViewById(R.id.hayvansearch);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(txt.getText().toString())) {

                    Toast.makeText(getContext(), "L??tfen K??pe Numaras?? Giriniz ", Toast.LENGTH_SHORT).show();
                }
                else{
                    sorgula(v);
                }
            }

        });

        Button btnn=genel.findViewById(R.id.okuu);
        btnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qroku(v);
            }
        });

        return genel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HayvansorgulaViewModel.class);
        // TODO: Use the ViewModel
    }

    public void sorgula(View v){


            tasima = txt.getText().toString();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFireStore = FirebaseFirestore.getInstance();
        docref= mFireStore.collection("hayvanlar").document(tasima);

        // K??pe No ya g??re Firestoredan  Hayvan Bilgileri ??ekme

        docref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            nvb.bilgieklehayvan(tasima);
                            NavDirections action = new ActionOnlyNavDirections(R.id.action_nav_HayvanSorgula_to_nav_HayvanBilgiGenel);
                            Navigation.findNavController(v).navigate(action);
                        }
                        else{
                            Toast.makeText(getContext(), "Hayvan Veri Taban??nda Kay??tl?? De??il", Toast.LENGTH_SHORT).show();
                            txt.setText("");
                        }
                    }
                });

    }

    public void qroku (View v){

        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(Hayvansorgula.this);
        //K??t??phanede bir ka?? kod tipi var biz hepsini tarayacak ??ekilde ??al????t??rd??k.
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        //??eklindede sadece qr code taratabilirsiniz.
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        //Kamera a????ld??????nda a??a????da yaz?? g??sterecek
        integrator.setPrompt("Scan");
        //telefonun kendi kameras??n?? kulland??r??caz
        integrator.setCameraId(0);
        //okudu??unda 'beep' sesi ????kar??r
        integrator.setBeepEnabled(true);
        //okunan barkodun image dosyas??n?? kaydediyor
        integrator.setBarcodeImageEnabled(false);
        //scan ba??lat??l??yor
        integrator.initiateScan();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {

                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();

            } else {

                mUser = FirebaseAuth.getInstance().getCurrentUser();
                mFireStore = FirebaseFirestore.getInstance();
                docref2= mFireStore.collection("hayvanlar").document(result.getContents());

                // K??pe No ya g??re Firestoredan  Hayvan Bilgileri ??ekme

                docref2.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    nvb.bilgieklehayvan(result.getContents());
                                    NavDirections action = new ActionOnlyNavDirections(R.id.action_nav_HayvanSorgula_to_nav_HayvanBilgiGenel);
                                    Navigation.findNavController(genel).navigate(action);
                                }
                                else{
                                    Toast.makeText(getContext(), "Hayvan Veri Taban??nda Kay??tl?? De??il", Toast.LENGTH_SHORT).show();
                                    txt.setText("");
                                }
                            }
                        });

            }
        }
    }

}