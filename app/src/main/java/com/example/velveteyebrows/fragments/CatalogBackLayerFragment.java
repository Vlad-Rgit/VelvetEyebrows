package com.example.velveteyebrows.fragments;

import android.os.Bundle;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.velveteyebrows.databinding.CatalogBackdropLayoutBinding;
import com.example.velveteyebrows.providers.DecimalPriceFormatProvider;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.io.Serializable;
import java.util.List;

public class CatalogBackLayerFragment extends Fragment {

    public static final String FILTER_STATE = "FilterState";
    public static final String MAX_PRICE_ARG = "MaxPriceArg";
    public static final String MIN_PRICE_ARG = "MinPriceArg";


    public class FilterState implements Serializable {
        
        
        private float _maxPrice;
        private float _minPrice;
        private float _currentMinPrice;
        private float _currentMaxPrice;
        private float _minDiscount;
        private boolean _isOnlyFav;

        private FilterState(){
            
        }
        
        private FilterState(float minPrice, float maxPrice){
            _minPrice = minPrice;
            _currentMinPrice = minPrice;
            _maxPrice = maxPrice;
            _currentMaxPrice = maxPrice;
        }
        
        public float getMaxPrice() {
            return _maxPrice;
        }

        public float getMinPrice() {
            return _minPrice;
        }

        public float getMinDiscount() {
            return _minDiscount;
        }

        public float getCurrentMinPrice() {
            return _currentMinPrice;
        }

        public float getCurrentMaxPrice() {
            return _currentMaxPrice;
        }

        public boolean isOnlyFav() {
            return _isOnlyFav;
        }
    }

    public interface OnFilterStateChangedCallback {
        void filter(FilterState filterState);
    }


    private FilterState _filterState;
    private OnFilterStateChangedCallback _onFilterStateChangedCallback;


    public CatalogBackLayerFragment(){

    }

    /**
     * Create new instance of fragment with inital values of max and min price for range slider
     * @param minPrice min price for range slider
     * @param maxPrice max price for range slider
     * @return new instance of CatalogBackLayerFragment
     */
    public static CatalogBackLayerFragment newInstance(float minPrice, float maxPrice){

        CatalogBackLayerFragment fragment = new CatalogBackLayerFragment();

        Bundle bundle = new Bundle();
        bundle.putFloat(MIN_PRICE_ARG, minPrice);
        bundle.putFloat(MAX_PRICE_ARG, maxPrice);

        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * Create fragment without initial values
     * @return new empty CatalogBackLayerFragment
     */
    public static CatalogBackLayerFragment newInsance(){
        return new CatalogBackLayerFragment();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if(args != null){
            
            _filterState = new FilterState(args.getFloat(MIN_PRICE_ARG), args.getFloat(MAX_PRICE_ARG));
        }
        else if(savedInstanceState != null){
            _filterState = (FilterState)
                    savedInstanceState.getSerializable(FILTER_STATE);
        }
        else{
            _filterState = new FilterState();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CatalogBackdropLayoutBinding binding =
                CatalogBackdropLayoutBinding.inflate(inflater, container, false);


        //Set initial values
        binding.rngSliderPrice.setValueFrom(_filterState._minPrice);
        binding.rngSliderPrice.setValueTo(_filterState._maxPrice);
        binding.rngSliderPrice.setValues(_filterState._currentMinPrice,
                _filterState._currentMaxPrice);

        if(_filterState._minDiscount > 0) {
            Chip chip = binding.chipGroupDiscounts
                    .findViewWithTag(String.valueOf(_filterState._minDiscount));
            binding.chipGroupDiscounts.check(chip.getId());
        }

        binding.tvMinPrice.setText(
                DecimalPriceFormatProvider
                        .getDefaultDecimalFormat().format(_filterState._currentMinPrice)
        );

        binding.tvMaxPrice.setText(
                DecimalPriceFormatProvider
                        .getDefaultDecimalFormat().format(_filterState._currentMaxPrice)
        );

        //Set callbacks
        binding.rngSliderPrice.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(RangeSlider slider, float value, boolean fromUser) {

                List<Float> values = slider.getValues();

                _filterState._currentMinPrice = values.get(0);
                _filterState._currentMaxPrice = values.get(1);

                binding.tvMinPrice.setText(
                        DecimalPriceFormatProvider
                                 .getDefaultDecimalFormat().format(_filterState._currentMinPrice)
                );

                binding.tvMaxPrice.setText(
                        DecimalPriceFormatProvider
                                .getDefaultDecimalFormat().format(_filterState._currentMaxPrice)
                );

                if(_onFilterStateChangedCallback != null) {
                    _onFilterStateChangedCallback.filter(_filterState);
                }

            }
        });

        binding.chipGroupDiscounts.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                if(checkedId == View.NO_ID){
                    _filterState._minDiscount = 0;
                }
                else {

                    Chip chip = group.findViewById(checkedId);

                    _filterState._minDiscount = Float.parseFloat(
                            chip.getTag().toString()
                    );
                }

                if(_onFilterStateChangedCallback != null) {
                    _onFilterStateChangedCallback.filter(_filterState);
                }
            }
        });

        binding.switchFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                _filterState._isOnlyFav = isChecked;

                if(_onFilterStateChangedCallback != null){
                    _onFilterStateChangedCallback.filter(_filterState);
                }
            }
        });

        return binding.getRoot();
    }

    public FilterState getFilterState(){
        return _filterState;
    }

    public void setFilterChangedCallback(OnFilterStateChangedCallback callback){
        _onFilterStateChangedCallback = callback;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
          //  outState.putSerializable(FILTER_STATE, _filterState);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
}