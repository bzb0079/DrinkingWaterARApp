package app.th.project.drinkingWaterAR.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import app.th.project.drinkingWaterAR.R;

import java.util.List;

public class CustomAdapterUtil extends BaseAdapter {


        List<String> complaintList;
        Activity context;
        boolean[] itemChecked;

        public CustomAdapterUtil(Activity context, List<String> complaintList) {
            super();
            this.context = context;
            this.complaintList = complaintList;
            itemChecked = new boolean[complaintList.size()];
        }

        private class ViewHolder {
            TextView complaintView;
            CheckBox ck1;
        }

        public int getCount() {
            return complaintList.size();
        }

        public Object getItem(int position) {
            return complaintList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            LayoutInflater inflater = context.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();

                holder.complaintView = (TextView) convertView
                        .findViewById(R.id.textView1);
                holder.ck1 = (CheckBox) convertView
                        .findViewById(R.id.checkBox1);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            String complaintDetail = complaintList.get(position);
            holder.complaintView.setCompoundDrawablePadding(15);
            holder.complaintView.setText(complaintDetail);
            holder.ck1.setChecked(false);

            if (itemChecked[position])
                holder.ck1.setChecked(true);
            else
                holder.ck1.setChecked(false);

            holder.ck1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (holder.ck1.isChecked())
                        itemChecked[position] = true;
                    else
                        itemChecked[position] = false;
                }
            });

            return convertView;

        }
}
