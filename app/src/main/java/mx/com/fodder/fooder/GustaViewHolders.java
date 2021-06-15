package mx.com.fodder.fooder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mx.com.fodder.fooder.chat.ChatActivity;

public class GustaViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mGustaID, mGustaName;
    public ImageView mGustaImage;
    public GustaViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mGustaID = (TextView) itemView.findViewById(R.id.GustaID);//tal vez problema con el "R"
        mGustaName = (TextView) itemView.findViewById(R.id.GustaName);
        mGustaImage = (ImageView) itemView.findViewById(R.id.GustaImage);


    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("gustaID", mGustaID.getText().toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }
}
