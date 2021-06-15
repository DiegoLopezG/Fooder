package mx.com.fodder.fooder.Cards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

import mx.com.fodder.fooder.R;

public class arrayAdapter extends ArrayAdapter<cards> {
    Context context;

    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super (context, resourceId, items);
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        cards card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.nombre);
        TextView instagram = (TextView) convertView.findViewById(R.id.instagram);
        ImageView image = (ImageView) convertView.findViewById(R.id.imagen);

        name.setText(card_item.getName());
        instagram.setText(card_item.getInstagram());

        switch (card_item.getProfileImageURL()){
            case "default":
                Glide.with(convertView.getContext()).load(R.mipmap.profilepicsmall).into(image);
                break;
            default:
                Glide.clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageURL()).into(image);
                break;
        }



        return convertView;
    }
}
