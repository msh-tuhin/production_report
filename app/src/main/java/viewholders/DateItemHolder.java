package viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.productionreport.bd.Hourly;
import com.productionreport.bd.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DateItemHolder extends RecyclerView.ViewHolder {

    LinearLayout parentLayout;
    TextView dateTV;

    public DateItemHolder(@NonNull View v) {
        super(v);
        parentLayout = v.findViewById(R.id.parent_layout);
        dateTV = v.findViewById(R.id.date_tv);
    }

    public void bindTo(final Context context,
                       final String fullDate,
                       final String section,
                       final String orderID){
        refresh();
        dateTV.setText(fullDate);

        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Hourly.class);
                intent.putExtra("section", section);
                intent.putExtra("order_id", orderID);
                intent.putExtra("full_date", fullDate);
                context.startActivity(intent);
            }
        });
    }

    private void refresh(){
        dateTV.setText("");
    }
}
