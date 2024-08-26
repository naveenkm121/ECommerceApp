package com.ecommerce.app.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.ecommerce.app.R
import com.ecommerce.app.constants.ScreenName
import com.ecommerce.app.constants.HomeViewTypeEnum
import com.ecommerce.app.data.address.AddressItem
import com.ecommerce.app.data.cart.CartItem
import com.ecommerce.app.data.category.Category
import com.ecommerce.app.data.home.ViewItemData
import com.ecommerce.app.databinding.ItemAddressBinding
import com.ecommerce.app.databinding.ItemBigBannerBinding
import com.ecommerce.app.databinding.ItemCartBinding
import com.ecommerce.app.databinding.ItemCategoryBinding
import com.ecommerce.app.databinding.ItemCategoryHorizontalBinding
import com.ecommerce.app.databinding.ItemHomeProductBinding
import com.ecommerce.app.databinding.ItemSmallBannerBinding
import com.ecommerce.app.utils.CommonSelectItemRVListerner
import com.ecommerce.app.utils.CommonUtility


class CommonRVAdapter(private val fromScreen: String, val listener: CommonSelectItemRVListerner) :
    RecyclerView.Adapter<CommonViewHolder>() {


    private val items = arrayListOf<Any>()

    fun <T> setItems(items: ArrayList<T>) {
        this.items.clear()
        this.items.addAll(items as ArrayList<*>)
        notifyDataSetChanged()
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        when (fromScreen) {

            HomeViewTypeEnum.SMALL_ICON_TYPE.value -> {

                val binding: ItemCategoryHorizontalBinding =
                    ItemCategoryHorizontalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return CommonViewHolder(parent.context, binding, fromScreen, listener)
            }

            HomeViewTypeEnum.SMALL_BANNER_TYPE.value -> {

                val binding: ItemSmallBannerBinding =
                    ItemSmallBannerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return CommonViewHolder(parent.context, binding, fromScreen, listener)
            }

            HomeViewTypeEnum.BIG_BANNER_TYPE.value -> {

                val binding: ItemBigBannerBinding =
                    ItemBigBannerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return CommonViewHolder(parent.context, binding, fromScreen, listener)
            }

            HomeViewTypeEnum.PRODUCT_CARD_TYPE.value,HomeViewTypeEnum.PRODUCT_CARD_BANNER_TYPE.value -> {

                val binding: ItemHomeProductBinding =
                    ItemHomeProductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return CommonViewHolder(parent.context, binding, fromScreen, listener)
            }

            ScreenName.FRAGMENT_HOME_TOP_CATEGORY.value -> {

                val binding: ItemCategoryHorizontalBinding =
                    ItemCategoryHorizontalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return CommonViewHolder(parent.context, binding, fromScreen, listener)
            }

            ScreenName.FRAGMENT_CATEGORY.value -> {

                val binding: ItemCategoryBinding =
                    ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CommonViewHolder(parent.context, binding, fromScreen, listener)
            }

            ScreenName.FRAGMENT_ADDRESS.value -> {

                val binding: ItemAddressBinding =
                    ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CommonViewHolder(parent.context, binding, fromScreen, listener)
            }

            ScreenName.FRAGMENT_CART.value -> {

                val binding: ItemCartBinding =
                    ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CommonViewHolder(parent.context, binding, fromScreen, listener)
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }

    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        holder.bind(items[position])
    }


}

class CommonViewHolder(
    private val context: Context,
    private val itemBinding: ViewBinding,
    private val fromScreen: String,
    private val listener: CommonSelectItemRVListerner
) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

    private lateinit var selectedItem: Any

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Any) {
        selectedItem = item
        when (fromScreen) {

            HomeViewTypeEnum.SMALL_ICON_TYPE.value -> {
                item as ViewItemData
                itemBinding as ItemCategoryHorizontalBinding
                itemBinding.nameTV.text = item.name
                Glide.with(context)
                    .load(item.src)
                    .into(itemBinding.iconIV)

            }

            HomeViewTypeEnum.SMALL_BANNER_TYPE.value -> {
                item as ViewItemData
                itemBinding as ItemSmallBannerBinding
             //   itemBindiproductImageView
               /* Glide.with(context)
                    .load(item.src)
                    .into(itemBinding.productImageView)*/
            }

            HomeViewTypeEnum.BIG_BANNER_TYPE.value -> {
                item as ViewItemData
                itemBinding as ItemBigBannerBinding
               //itemBinding.root.layoutParams.height=context.resources.getDimensionPixelSize(R.dimen.height300)
                 Glide.with(context)
                     .load(item.src)
                     .into(itemBinding.backgroundImageView)
            }

            HomeViewTypeEnum.PRODUCT_CARD_TYPE.value,HomeViewTypeEnum.PRODUCT_CARD_BANNER_TYPE.value -> {
                item as ViewItemData
                itemBinding as ItemHomeProductBinding

                //itemBinding.productBrandTV.text=item!!.name
                itemBinding.productNameTV.text=item.name
                itemBinding.discountPriceTV.text=context.getString(R.string.input_rs_symbol,item.price.toString())
                itemBinding.priceTV.text=context.getString(R.string.input_rs_symbol,item.price.toString())
                itemBinding.discountTV.text="${item.discountPercentage}%"
                itemBinding.priceTV.setPaintFlags(itemBinding.priceTV.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                Glide.with(context)
                    .load(item.src)
                    .into(itemBinding.productImageView)

                //itemBinding.cardView.setBackgroundColor(mContext.getColor(R.color.light_blue))
                // itemBinding.cardView.setBackgroundResource(R.drawable.ic_facebook);
            }

            ScreenName.FRAGMENT_CATEGORY.value -> {
                item as Category
                itemBinding as ItemCategoryBinding
                itemBinding.nameTV.text = item.name
            }

            ScreenName.FRAGMENT_ADDRESS.value -> {
                item as AddressItem
                itemBinding as ItemAddressBinding
                itemBinding.nameTV.text = item.name
                itemBinding.address1TV.text=item.address1
                itemBinding.address2TV.text=item.address2+", "+item.locality
                itemBinding.landmark.text=context.getString(R.string.landmark_str,item.landmark)
                itemBinding.cityPincodeTV.text= item.city+"-"+item.pincode
                itemBinding.state.text= item.state
                itemBinding.mobileTV.text= context.getString(R.string.mobile_str,item.mobile)

                if(item.isDefault==1)
                {
                    itemBinding.defaultIV.setColorFilter(context.getColor(R.color.red))
                    itemBinding.defaultTV.setTextColor(context.getColor(R.color.red))
                    itemBinding.defaultCheckIV.visibility=View.VISIBLE
                }else{
                    itemBinding.defaultIV.setColorFilter(context.getColor(R.color.grey_dark))
                    itemBinding.defaultTV.setTextColor(context.getColor(R.color.grey_dark))
                    itemBinding.defaultCheckIV.visibility=View.GONE
                }

                itemBinding.deleteLL.setOnClickListener {
                    listener.onSelectItemRVType(item,ScreenName.ACTION_DELETE_ADDRESS.value)
                }
                itemBinding.editLL.setOnClickListener {
                    listener.onSelectItemRVType(item,ScreenName.ACTION_EDIT_ADDRESS.value)
                }
                itemBinding.defaultLL.setOnClickListener {
                    listener.onSelectItemRVType(item,ScreenName.ACTION_DEFAULT_ADDRESS.value)
                }
            }

            ScreenName.FRAGMENT_CART.value -> {
                item as CartItem
                itemBinding as ItemCartBinding
                itemBinding.productBrandTV.text = item.brand
                itemBinding.productNameTV.text=item.title
                itemBinding.discountPriceTV.text=context.getString(R.string.input_rs_symbol,CommonUtility.roundOffToTwoDecimalPlaces(item.discountPrice))
                itemBinding.priceTV.text=context.getString(R.string.input_rs_symbol,CommonUtility.roundOffToTwoDecimalPlaces(item.price))
                itemBinding.discountTV.text="${item.discount_per}%"
                itemBinding.priceTV.setPaintFlags(itemBinding.priceTV.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                //itemBinding.quantityTV.text=context.getString(R.string.input_quantity,item.quantity.toString())
                itemBinding.editQuantityTV.text=item.quantity.toString()

                Glide.with(context)
                    .load(item.thumbnail)
                    .into(itemBinding.productImageView)

                itemBinding.deleteIV.setOnClickListener {
                    listener.onSelectItemRVType(item,ScreenName.ACTION_DELETE_CART.value)
                }

                itemBinding.minusBTN.setOnClickListener {
                    var quantity= item.quantity
                    quantity=quantity-1
                    if(quantity<0) quantity=0
                    item.quantity=quantity;
                    itemBinding.editQuantityTV.text=item.quantity.toString()
                    listener.onSelectItemRVType(item,ScreenName.ACTION_MINUS_FROM_CART.value)
                }
                itemBinding.plusBTN.setOnClickListener {

                    var quantity= item.quantity
                    item.quantity=quantity+1;
                    itemBinding.editQuantityTV.text=item.quantity.toString()
                    listener.onSelectItemRVType(item,ScreenName.ACTION_ADD_ITEM_TO_CART.value)
                }
            }

        }

    }

    override fun onClick(p0: View?) {
        listener.onSelectItemRVType(selectedItem,"")
    }

}
