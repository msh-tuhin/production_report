package viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.productionreport.bd.EditValue;
import com.productionreport.bd.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import models.DataModel;

public class HourlyItemHolder extends RecyclerView.ViewHolder {

    TextView timeTV;
    TextView countTV;
    ImageView edit;

    public HourlyItemHolder(@NonNull View v) {
        super(v);
        timeTV = v.findViewById(R.id.time_tv);
        countTV = v.findViewById(R.id.count_tv);
        edit = v.findViewById(R.id.edit);
    }

    public void bindTo(final Context context, final DataModel dataModel, final String dataID){
        refresh();
        String[] slots = context.getResources().getStringArray(R.array.time_slots);
        timeTV.setText(slots[dataModel.getTimeBucket().intValue()]);
        countTV.setText(Long.toString(dataModel.getCount()));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditValue.class);
                intent.putExtra("count", dataModel.getCount());
                intent.putExtra("id", dataID);
                context.startActivity(intent);
            }
        });
    }

    private void refresh(){
        timeTV.setText("");
        countTV.setText("");
    }
}
