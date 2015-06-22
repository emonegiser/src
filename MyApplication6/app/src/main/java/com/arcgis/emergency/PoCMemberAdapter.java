package com.arcgis.emergency;

import android.content.Context;
import android.text.TextUtils;
import android.widget.SimpleAdapter;

import com.arcgis.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对讲成员列表
 * Created by jiangwj on 2015/1/27.
 */
public class PoCMemberAdapter extends SimpleAdapter {

    private List<Map<String, Object>> data;

    public PoCMemberAdapter(Context context) {
        this(context, new ArrayList<Map<String, Object>>());
    }

    public PoCMemberAdapter(Context context, List<Map<String, Object>> data) {
        super(context, data,
                R.layout.item_poc_member,
                new String[] { "tel", "talking" },
                new int[] { R.id.textView, R.id.talking });
        this.data = data;
    }

    public void clearMembers() {
        data.clear();
        //
        notifyDataSetChanged();
    }

    private void addItem(Map<String, Object> item) {
        data.add(item);
    }

    public void addMember(String tel) {
        if (TextUtils.isEmpty(tel)) return;
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("tel", tel);
        item.put("talking", Boolean.FALSE);
        //
        addItem(item);
        //
        notifyDataSetChanged();
    }

    public void checkAndAddMember(String tel) {
        if (TextUtils.isEmpty(tel)) return;
        for (Map<String, ?> mem : data) {
            if (mem == null) continue;
            if (tel.equals(mem.get(tel))) return;
        }
        //
        addMember(tel);
    }

    public void removeMember(final String tel) {
        if (TextUtils.isEmpty(tel)) return;
        for (int i = data.size() - 1; i >= 0; i--) {
            Map<String, ?> mem = data.get(i);
            if (mem == null || tel.equals(mem.get(tel))) {
                data.remove(i);
            }
        }
        //
        notifyDataSetChanged();
    }

    public void updateCurrentSpeaker(final String tel) {
        for (Map<String, ?> m : data) {
            if (m == null) continue;
            Map<String, Object> mem = (Map<String, Object>) m;
            if (tel != null && tel.equals(mem.get("tel"))) {
                mem.put("talking", Boolean.TRUE);
            } else {
                mem.put("talking", Boolean.FALSE);
            }
        }
        //
        notifyDataSetChanged();
    }

}
