package viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.productionreport.bd.ApproveMember;
import com.productionreport.bd.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.FirestoreFieldNames;

public class MemberItemHolder extends RecyclerView.ViewHolder {

    LinearLayout parentLayout;
    TextView nameTV;
    TextView statusTV;
    TextView sectionTV;

    private String[] str = {"Admin", "Admin", "Supervisor", "Pending"};

    public MemberItemHolder(@NonNull View v) {
        super(v);
        parentLayout = v.findViewById(R.id.parent_layout);
        nameTV = v.findViewById(R.id.name_tv);
        statusTV = v.findViewById(R.id.status_tv);
        sectionTV = v.findViewById(R.id.section_tv);
    }

    public void bindTo(final Context context, final String personLink){
        refresh();
        FirebaseFirestore.getInstance().collection("person_vital")
                .document(personLink)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            bindName(documentSnapshot);
                            bindStatus(documentSnapshot);
                            bindSection(documentSnapshot);
                            setParentLayoutOnClickListener(context, documentSnapshot, personLink);
                        }
                    }
                });
    }

    private void bindName(DocumentSnapshot documentSnapshot){
        String name = documentSnapshot.getString(FirestoreFieldNames.PERSON_NAME);
        if(name != null){
            nameTV.setText(name);
        }
    }

    private void bindStatus(DocumentSnapshot documentSnapshot){
        Long status = documentSnapshot.getLong(FirestoreFieldNames.PERSON_STATUS);
        if(status != null){
            statusTV.setText(str[status.intValue()]);
        }
    }

    private void bindSection(DocumentSnapshot documentSnapshot){
        String section = documentSnapshot.getString(FirestoreFieldNames.PERSON_SECTION);
        if(section != null && !section.equals("")){
            sectionTV.setText("Section: " + section);
            sectionTV.setVisibility(View.VISIBLE);
        }
    }

    private void setParentLayoutOnClickListener(final Context context,
                                                final DocumentSnapshot documentSnapshot,
                                                final String personLink){
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Long status = documentSnapshot.getLong(FirestoreFieldNames.PERSON_STATUS);
                if(!personLink.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Intent intent = new Intent(context, ApproveMember.class);
                    intent.putExtra("personLink", personLink);
                    intent.putExtra("name", documentSnapshot.getString(FirestoreFieldNames.PERSON_NAME));
                    context.startActivity(intent);
                }
            }
        });
    }

    private void refresh(){
        nameTV.setText("");
        statusTV.setText("");
        sectionTV.setText("");
        sectionTV.setVisibility(View.GONE);
    }
}
