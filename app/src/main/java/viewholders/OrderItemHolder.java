package viewholders;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.productionreport.bd.R;
import com.productionreport.bd.Sections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import models.OrderModel;
import myapp.utils.PictureBinder;

public class OrderItemHolder extends RecyclerView.ViewHolder {

    ImageView image;
    TextView nameTV;
    TextView quantityTV;
    LinearLayout parentLayout;

    public OrderItemHolder(@NonNull View v) {
        super(v);
        image = v.findViewById(R.id.image);
        nameTV = v.findViewById(R.id.name_tv);
        quantityTV = v.findViewById(R.id.quantity_tv);
        parentLayout = v.findViewById(R.id.parent_layout);
    }

    public void bindTo(OrderModel orderModel, final Context context, final String orderID){
        refreshHolder();
        String name = orderModel.getName();
        if(name != null) nameTV.setText(name);
        Long quantity = orderModel.getQuantity();
        if(quantity != null) quantityTV.setText("Quantity: " + Long.toString(quantity));
        PictureBinder.bindProfilePicture(image, orderModel.getPicture());

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Sections.class);
                intent.putExtra("order_id", orderID);
                context.startActivity(intent);
            }
        });
    }

    private void refreshHolder(){
        nameTV.setText("");
        quantityTV.setText("");
        image.setImageResource(R.drawable.ltgray);
    }
}
