package myapp.utils;

import android.widget.ImageView;

import com.productionreport.bd.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class PictureBinder {
    public static void bindProfilePicture(ImageView view, DocumentSnapshot documentSnapshot){
        if(documentSnapshot == null) return;
        String profilePictureLink = documentSnapshot.getString("pp");
        if(profilePictureLink != null){
            if(profilePictureLink.equals("")){
                view.setImageResource(R.drawable.ltgray);
            }else{
                Picasso.get().load(profilePictureLink)
                        .placeholder(R.drawable.ltgray)
                        .error(R.drawable.ltgray)
                        .into(view);
            }
        }
    }

    public static void bindProfilePicture(ImageView view, String profilePictureLink){
        if(profilePictureLink != null){
            if(profilePictureLink.equals("")){
                view.setImageResource(R.drawable.ltgray);
            }else{
                Picasso.get().load(profilePictureLink)
                        .placeholder(R.drawable.ltgray)
                        .error(R.drawable.ltgray)
                        .into(view);
            }
        }
    }

    public static void bindCoverPicture(ImageView view, DocumentSnapshot documentSnapshot){
        if(documentSnapshot == null) return;
        String profilePictureLink = documentSnapshot.getString("cp");
        if(profilePictureLink != null){
            if(profilePictureLink.equals("")){
                view.setImageResource(R.drawable.ltgray);
            }else{
                Picasso.get().load(profilePictureLink)
                        .placeholder(R.drawable.ltgray)
                        .error(R.drawable.ltgray)
                        .into(view);
            }
        }
    }

    public static void bindPictureSearchResult(ImageView view, String imageUrlString){
        if(imageUrlString!=null && !imageUrlString.equals("")){
            Picasso.get().load(imageUrlString)
                    .placeholder(R.drawable.ltgray)
                    .error(R.drawable.ltgray)
                    .into(view);
        }
    }
}
