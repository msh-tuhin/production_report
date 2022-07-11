package viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.productionreport.bd.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShiftItemHolder extends RecyclerView.ViewHolder {

    String[] shifts = {"8AM to 1PM", "2PM to 5PM", "5PM to 9PM", "10PM to 3AM"};

    TextView timeTV;
    TextView countTV;
    ImageView edit;

    public ShiftItemHolder(@NonNull View v) {
        super(v);
        timeTV = v.findViewById(R.id.time_tv);
        countTV = v.findViewById(R.id.count_tv);
        edit = v.findViewById(R.id.edit);
    }

    public void bindTo(int position, String data){
        refresh();
        timeTV.setText(shifts[position]);
        countTV.setText(data);
        edit.setVisibility(View.INVISIBLE);
    }

    private void refresh(){
        timeTV.setText("");
        countTV.setText("");
    }
}
