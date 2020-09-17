package com.example.velveteyebrows.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import com.velveteyebrows.dto.ServiceDTO;

import java.util.Comparator;

public class ServiceSortedListCallback extends SortedList.Callback<ServiceDTO> {

    private final Comparator<ServiceDTO> _comparator;
    private final RecyclerView.Adapter<ServiceRecycleAdapter.ViewHolder> _adapter;

    public ServiceSortedListCallback(Comparator<ServiceDTO> comparator,
                                     RecyclerView.Adapter<ServiceRecycleAdapter.ViewHolder> adapter) {
        _comparator = comparator;
        _adapter = adapter;
    }

    @Override
    public int compare(ServiceDTO o1, ServiceDTO o2) {
        return _comparator.compare(o1,o2);
    }

    @Override
    public void onChanged(int position, int count) {
        _adapter.notifyItemRangeChanged(position, count);
    }

    @Override
    public boolean areContentsTheSame(ServiceDTO oldItem, ServiceDTO newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areItemsTheSame(ServiceDTO item1, ServiceDTO item2) {
        return item1.getId() == item2.getId();
    }

    @Override
    public void onInserted(int position, int count) {
        _adapter.notifyItemRangeInserted(position,count);
    }

    @Override
    public void onRemoved(int position, int count) {
        _adapter.notifyItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        _adapter.notifyItemMoved(fromPosition, toPosition);
    }
}
