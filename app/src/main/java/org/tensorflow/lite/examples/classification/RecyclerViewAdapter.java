package org.tensorflow.lite.examples.classification;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    /**上方的arrayList為RecyclerView所綁定的ArrayList*/
    ArrayList<HashMap<String,String>> arrayList;
    /**儲存最原先ArrayList的狀態(也就是充當回複RecyclerView最原先狀態的陣列)*/
    ArrayList<HashMap<String,String>> arrayListFilter;
    MainActivity_mknow mainActivity;
    String mstype,mtel;
    public RecyclerViewAdapter(ArrayList<HashMap<String,String>> arrayList, MainActivity_mknow mActivity,String stype) {
        mainActivity=mActivity;
        mstype=stype;
        this.arrayList = arrayList;
        arrayListFilter = new ArrayList<HashMap<String,String>>();
        /**這裡把初始陣列複製進去了*/
        arrayListFilter.addAll(arrayList);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvPos,tvType,tvPrice,tvCar,tvDateTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPos = itemView.findViewById(R.id.textView_pos);
            tvType = itemView.findViewById(R.id.textView_type);
            tvPrice = itemView.findViewById(R.id.textView_price);
            tvCar = itemView.findViewById(R.id.textView_car);
            tvDateTime = itemView.findViewById(R.id.textView_time);
            /**點擊事件*/
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mstype.contains("know")) {
                //AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create(); //Read Update
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity); //Read Update
                //取得自訂的版面。
                LayoutInflater inflater = LayoutInflater.from(mainActivity);
                final View w = inflater.inflate(R.layout.showdata, null);
                alertDialog.setTitle("醫療保健知識");
                alertDialog.setView(w);
                WebView web;
                web = (WebView) w.findViewById(R.id.web);
                web.setHorizontalScrollBarEnabled(true);
                WebSettings webSettings = web.getSettings();
                //
                webSettings.setJavaScriptEnabled(true);
                webSettings.setUseWideViewPort(true);
                //WebViewClient:影響View的事件處理
                WebViewClient webViewClient = new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        return false;
                    }
                };
                web.setWebViewClient(webViewClient);
                web.loadUrl(arrayList.get(getAdapterPosition()).get("weblink")); //網址
                Log.d("TAG", "tvCar: " + arrayList.get(getAdapterPosition()).get("weblink"));

                // alertDialog.setMessage("商家地址:  " + arrayList.get(getAdapterPosition()).get("user_location") + "\n"
                //         + "商家資訊:  " + arrayList.get(getAdapterPosition()).get("user_introduce") + "\n"
                //         + "用餐方式: " + arrayList.get(getAdapterPosition()).get("meal") + "\n"
                //         + "菜單:"+ arrayList.get(getAdapterPosition()).get("user_menu") );

                alertDialog.setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });
                alertDialog.show();
            }
            else
            {
             // call phone
                //
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setMessage("確認打電話給診所?");
                builder.setPositiveButton("撥給診所", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                        mtel=arrayList.get(getAdapterPosition()).get("dates");
                        call_sos();
                        //
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                //
            }
        }
    }
    //
    public void call_sos() {

        // grand permission
        if (ContextCompat.checkSelfPermission(mainActivity.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.CALL_PHONE}, 20);
        }//
        else {


            sendMessage();

        }  //

    }
//
//
public void sendMessage() {

    //call phone
    Intent it = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + mtel));
    if (ContextCompat.checkSelfPermission(mainActivity,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.CALL_PHONE}, 20);
        //return;
    }
    else
        mainActivity.startActivity(it);
    //

    //
}
    //
    //
   // @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            sendMessage();
        } else
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 20);
            return;
    }

    //
//
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mstype=="know") {
            holder.tvPos.setText(arrayList.get(position).get("tit"));
            holder.tvType.setText("資料來源:" + arrayList.get(position).get("datafrom"));
            holder.tvPrice.setText("資料時間:" + arrayList.get(position).get("dates"));
            holder.tvCar.setText("內容網址:" + arrayList.get(position).get("weblink"));
            //  holder.tvDateTime.setText("結束營業時間："+arrayList.get(position).get("user_time_close"));
        }
        else {
            holder.tvPos.setText(arrayList.get(position).get("tit"));
            holder.tvType.setText("地址:" + arrayList.get(position).get("datafrom"));
            holder.tvPrice.setText("電話:" + arrayList.get(position).get("dates"));
            holder.tvCar.setText("專長:" + arrayList.get(position).get("weblink"));
        }

        }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
    /**使用Filter濾除方法*/
    Filter mFilter = new Filter() {
        /**此處為正在濾除字串時所做的事*/
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            /**先將完整陣列複製過去*/
            ArrayList<HashMap<String,String>> filteredList = new ArrayList<HashMap<String,String>>();
            /**如果沒有輸入，則將原本的陣列帶入*/
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayListFilter);
            } else {
                /**如果有輸入，則照順序濾除相關字串
                 * 如果你有更好的搜尋演算法，就是寫在這邊*/
                for (int i = 0; i < arrayListFilter.size(); i++) {
                    HashMap<String,String> content = (HashMap<String,String>) arrayListFilter.get(i);
                    for(HashMap.Entry<String, String> entry : content.entrySet())
                    {

                        if(entry.getValue().contains(constraint) )
                        {
                            filteredList.add(content);
                            break;
                        }
                    }

                }
            }
            /**回傳濾除結果*/
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }
        /**將濾除結果推給原先RecyclerView綁定的陣列，並通知RecyclerView刷新*/
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList.clear();
            arrayList.addAll((Collection<? extends  HashMap<String,String>>) results.values);
            notifyDataSetChanged();
        }
    };
}
