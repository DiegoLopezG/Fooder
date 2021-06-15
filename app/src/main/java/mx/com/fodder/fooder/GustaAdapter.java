package mx.com.fodder.fooder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

public class GustaAdapter extends RecyclerView.Adapter<GustaViewHolders> {

    private List<GustaObject> gustaList;
    private Context context;


    public GustaAdapter (List<GustaObject> gustaList, Context context){
        this.gustaList = gustaList;
        this.context = context;
    }

    @Override
    public GustaViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gusta, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        GustaViewHolders rcv = new GustaViewHolders((layoutView));

        return rcv;
    }

    @Override
    public void onBindViewHolder(GustaViewHolders holder, int position) {
        holder.mGustaID.setText(gustaList.get(position).getUserId());
        holder.mGustaID.setTextColor(Color.parseColor("#F4E3E3"));
        holder.mGustaName.setText(gustaList.get(position).getName());
        if (!gustaList.get(position).getImagenPerfilURL().equals("default")){
            Glide.with(context).load(gustaList.get(position).getImagenPerfilURL()).into(holder.mGustaImage);
        }

    }

    @Override
    public int getItemCount() {
        return gustaList.size();
    }
}
