package tech01knowledge.blogspot.ecomtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tech01knowledge.blogspot.ecomtest.MainActivity.showCart;
import static tech01knowledge.blogspot.ecomtest.RegisterActivity.setSignUpFragment;


public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;

    public static boolean fromSearch = false;

    /*********** PRODUCT DESCRIPTION ******/
    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productImagesViewPager;
    private TabLayout viewpagerIndicator;
    private TextView productOnlyDescriptionBody;
    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();

    private boolean inStock = false;

    private String productDescription;
    private String productOtherDetails;

    /*********** PRODUCT DESCRIPTION ******/


    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView totalRatingMiniView;
    private TextView productPrice;
    private String productOriginalPrice;
    private TextView cuttedPrice;
    private ImageView codIndicator;
    private TextView tvCodIndicator;
    private TabLayout viewPagerIndicator;
    private TextView averageRating;

    private LinearLayout coupenRedemptionLayout;
    private Button coupenRedeemBtn;

    private TextView rewardTitle;
    private TextView rewardBody;


    private TabLayout productDetailsTablayout;

    private Button buyNowBtn;
    private LinearLayout addToCartBtn;
    public static MenuItem cartItem;

    /*********** RATINGS LAYOUT ******/
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNoContainer;
    private LinearLayout ratingsProgressBarContainer;
    private TextView totalRatingsFig;

    /*********** RATINGS LAYOUT ******/



    public static FloatingActionButton addToWishlisttBtn;
    public static boolean ALREDY_ADDED_TO_WISHLIST = false;
    public static boolean ALREDY_ADDED_TO_CART = false;

    public static Activity productDetailsActivity;

    private FirebaseFirestore firebaseFirestore;
    // Coupen Dialog
    private TextView coupenTitle;
    private TextView coupenExpiryDate;
    private TextView coupenBody;
    private RecyclerView coupenRecyclerView;
    private LinearLayout selectedCoupen;
    private TextView discountedPrice;
    private TextView originalPrice;
    // coupen Dialg
    private ViewPager productsDetailsViewpager;
    private Dialog loadingDialog;
    private Dialog signInDialog;
    private FirebaseUser currentUser;
    public static String productID;
    private TextView badgeCount;
    private DocumentSnapshot documentSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);          // sets back button

        totalRatingMiniView = findViewById(R.id.total_ratings_miniview);
        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewpagerIndicator = findViewById(R.id.viewpager_indicator);
        buyNowBtn = findViewById(R.id.buy_now_btn);
        addToWishlisttBtn= findViewById(R.id.add_to_wishlist_btn);
        productsDetailsViewpager = findViewById(R.id.product_details_viewpager);
        productDetailsTablayout = findViewById(R.id.product_details_tablayout);
        coupenRedeemBtn = findViewById(R.id.coupon_redemtion_btn);
        productTitle = findViewById(R.id.product_title);
        averageRatingMiniView = findViewById(R.id.tv_product_rating_miniview);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        codIndicator = findViewById(R.id.cod_indicator_imageview);
        tvCodIndicator = findViewById(R.id.tv_cod_indicator);
        rewardTitle = findViewById(R.id.reward_title);
        rewardBody = findViewById(R.id.reward_body);
        productDetailsTabsContainer=findViewById(R.id.product_details_tab_container);
        productDetailsOnlyContainer=findViewById(R.id.product_details_container);
        productOnlyDescriptionBody=findViewById(R.id.product_details_body);
        totalRatings=findViewById(R.id.total_ratings);
        ratingsNoContainer=findViewById(R.id.ratings_numbers_container);
        totalRatingsFig=findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer=findViewById(R.id.ratings_progressbar_container);
        averageRating=findViewById(R.id.average_rating);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        coupenRedemptionLayout = findViewById(R.id.coupon_redemtion_layout);

        initialRating = -1;

        //// LOading Dialog
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ///Loading dialog


        // Coupen Dialog

        final Dialog checkCoupenPriceDialog = new Dialog(ProductDetailsActivity.this);
        checkCoupenPriceDialog.setContentView(R.layout.coupen_redeem_dialog);
        checkCoupenPriceDialog.setCancelable(true);
        checkCoupenPriceDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView toggleRecyclerView = checkCoupenPriceDialog.findViewById(R.id.toggle_recyclerview);
        coupenRecyclerView = checkCoupenPriceDialog.findViewById(R.id.coupens_recyclerview);
        selectedCoupen = checkCoupenPriceDialog.findViewById(R.id.selected_coupen);

        coupenTitle = checkCoupenPriceDialog.findViewById(R.id.coupen_title);
        coupenExpiryDate = checkCoupenPriceDialog.findViewById(R.id.coupen_validity);
        coupenBody = checkCoupenPriceDialog.findViewById(R.id.coupen_body);

        originalPrice = checkCoupenPriceDialog.findViewById(R.id.original_price);
        discountedPrice = checkCoupenPriceDialog.findViewById(R.id.discount_price);



        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        coupenRecyclerView.setLayoutManager(layoutManager);



        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRecyclerView();
            }
        });

        // Coupen Dialog


        firebaseFirestore = FirebaseFirestore.getInstance();
        final List<String> productImages = new ArrayList<>();

        productID = getIntent().getStringExtra("PRODUCT_ID");
        firebaseFirestore.collection("PRODUCTS").document(productID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();

                            firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                                    productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                                }

                                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                                // I think here one more line of code will take place
                                                productImagesViewPager.setAdapter(productImagesAdapter);
                                                productTitle.setText(documentSnapshot.get("product_title").toString());
                                                averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                                                totalRatingMiniView.setText("(" + (long) documentSnapshot.get("total_ratings") + ") ratings");
                                                productPrice.setText("Rs." + documentSnapshot.get("product_price").toString() + "/-");

                                                //// for Coupen Dialog
                                                originalPrice.setText(productPrice.getText());
                                                productOriginalPrice =  documentSnapshot.get("product_price").toString();
                                                MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(DBqueries.rewardModelList,true,coupenRecyclerView, selectedCoupen, productOriginalPrice, coupenTitle, coupenExpiryDate, coupenBody, discountedPrice);
                                                coupenRecyclerView.setAdapter(myRewardsAdapter);
                                                myRewardsAdapter.notifyDataSetChanged();
                                                //// for Coupen Dialog

                                                cuttedPrice.setText("Rs." + documentSnapshot.get("cutted_price").toString() + "/-");

                                                if((boolean) documentSnapshot.get("COD")) {
                                                    codIndicator.setVisibility(View.VISIBLE);
                                                    tvCodIndicator.setVisibility(View.VISIBLE);
                                                } else {
                                                    codIndicator.setVisibility(View.INVISIBLE);
                                                    tvCodIndicator.setVisibility(View.INVISIBLE);
                                                }

                                                rewardTitle.setText((long)documentSnapshot.get("free_coupens") + documentSnapshot.get("free_coupen_title").toString());
                                                rewardBody.setText(documentSnapshot.get("free_coupen_body").toString());

                                                if((boolean)documentSnapshot.get("use_tab_layout")){
                                                    productDetailsTabsContainer.setVisibility(View.VISIBLE);
                                                    productDetailsOnlyContainer.setVisibility(View.GONE);
                                                    productDescription = documentSnapshot.get("product_description").toString();
                                                    productOtherDetails = documentSnapshot.get("product_other_details").toString();
                                                    for(long x = 1;x<(long)documentSnapshot.get("total_spec_titles")+1;x++){
                                                        productSpecificationModelList.add(new ProductSpecificationModel(0,documentSnapshot.get("spec_title_"+x).toString()));
                                                        for(long y =1;y<(long)documentSnapshot.get("spec_title_"+x+"_total_fields")+1;y++){
                                                            productSpecificationModelList.add(new ProductSpecificationModel(1,documentSnapshot.get("spec_title_"+x+"_field_"+y+"_name").toString(),documentSnapshot.get("spec_title_"+x+"_field_"+y+"_value").toString()));

                                                        }
                                                    }
                                                }else{
                                                    productDetailsTabsContainer.setVisibility(View.GONE);
                                                    productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                                    productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                                }
                                                totalRatings.setText((long)documentSnapshot.get("total_ratings") + " ratings");
                                                for(int x=0;x<5;x++){
                                                    TextView ratings = (TextView) ratingsNoContainer.getChildAt(x);
                                                    ratings.setText(String.valueOf((long)documentSnapshot.get((5-x)+"_star")));
                                                    ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                    int maxProgress = Integer.parseInt(String.valueOf((long)documentSnapshot.get("total_ratings")));
                                                    progressBar.setMax(maxProgress);
                                                    progressBar.setProgress(Integer.parseInt(String.valueOf((long)documentSnapshot.get((5-x)+"_star"))));
                                                }
                                                totalRatingsFig.setText(String.valueOf((long)documentSnapshot.get("total_ratings")));
                                                averageRating.setText(documentSnapshot.get("average_rating").toString());
                                                productsDetailsViewpager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTablayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));

                                                if (currentUser != null) {
                                                    if (DBqueries.myRating.size() == 0) {
                                                        DBqueries.loadRatingList(ProductDetailsActivity.this);
                                                    }
                                                    if (DBqueries.cartList.size() == 0) {
                                                        DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                                                    }
                                                    if (DBqueries.wishList.size() == 0) {
                                                        DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
//                                    DBqueries.loadRatingList(ProductDetailsActivity.this);
                                                    }
                                                    if (DBqueries.rewardModelList.size() == 0) {
                                                        DBqueries.loadRewards(ProductDetailsActivity.this, loadingDialog, false);
                                                    }
                                                    if (DBqueries.cartList.size() != 0 && DBqueries.wishList.size() != 0 && DBqueries.rewardModelList.size() != 0) {
                                                        loadingDialog.dismiss();
                                                    }
                                                } else {
                                                    loadingDialog.dismiss();
                                                }

                                                if (DBqueries.myRatedIds.contains(productID)) {
                                                    int index = DBqueries.myRatedIds.indexOf(productID);
                                                    initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) -1;
                                                    setRating(initialRating);
                                                }

                                                if (DBqueries.cartList.contains(productID)) {
                                                    ALREDY_ADDED_TO_CART = true;

                                                } else {
                                                    ALREDY_ADDED_TO_CART = false;
                                                }

                                                if (DBqueries.wishList.contains(productID)) {
                                                    ALREDY_ADDED_TO_WISHLIST = true;
                                                    addToWishlisttBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));

                                                } else {
                                                    addToWishlisttBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                                    ALREDY_ADDED_TO_WISHLIST = false;
                                                }

                                                if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {

                                                    inStock = true;
                                                    buyNowBtn.setVisibility(View.VISIBLE);

                                                    addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (currentUser == null) {
                                                                signInDialog.show();
                                                            } else {
                                                                if (!running_cart_query) {
                                                                    running_cart_query = true;
                                                                    if (ALREDY_ADDED_TO_CART) {
                                                                        running_cart_query = false;
                                                                        Toast.makeText(ProductDetailsActivity.this, "Already added to cart", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Map<String, Object> addProduct = new HashMap<>();
                                                                        addProduct.put("product_ID_" + String.valueOf(DBqueries.cartList.size()), productID);
                                                                        addProduct.put("list_size", (long) (DBqueries.cartList.size() + 1));

                                                                        firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART")
                                                                                .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    if (DBqueries.cartItemModelList.size() != 0) {
                                                                                        DBqueries.cartItemModelList.add(0,new CartItemModel(documentSnapshot.getBoolean("COD"),CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                                                                                                , documentSnapshot.get("product_title").toString()
                                                                                                , (long) documentSnapshot.get("free_coupens")
                                                                                                , documentSnapshot.get("product_price").toString()
                                                                                                , documentSnapshot.get("cutted_price").toString()
                                                                                                , (long) 1
                                                                                                , (long) documentSnapshot.get("offers_applied")
                                                                                                , (long) 0
                                                                                                , inStock
                                                                                                , (long) documentSnapshot.get("max_quantity")
                                                                                                , (long) documentSnapshot.get("stock_quantity")));
                                                                                    }
                                                                                    ALREDY_ADDED_TO_CART = true;
                                                                                    DBqueries.cartList.add(productID);
                                                                                    Toast.makeText(ProductDetailsActivity.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();
                                                                                    invalidateOptionsMenu();
                                                                                    running_cart_query = false;
                                                                                }

                                                                                else {
//                                        addToWishlisttBtn.setEnabled(true);
                                                                                    running_cart_query = false;
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            }

                                                        }
                                                    });

                                                } else {

                                                    inStock= false;
                                                    buyNowBtn.setVisibility(View.GONE);
                                                    TextView outOfStock = (TextView) addToCartBtn.getChildAt(0);
                                                    outOfStock.setText("Out of stock");
                                                    outOfStock.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                    outOfStock.setCompoundDrawables(null, null,null,null);

                                                }

                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
//        List<Integer> productImages = new ArrayList<>();
//        productImages.add(R.drawable.mob3);
//        productImages.add(R.drawable.ad);
//        productImages.add(R.drawable.mob4);
//        productImages.add(R.drawable.mob2);

        viewpagerIndicator.setupWithViewPager(productImagesViewPager, true);

        addToWishlisttBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
//                    addToWishlisttBtn.setEnabled(false);
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ALREDY_ADDED_TO_WISHLIST) {
                            int index = DBqueries.wishList.indexOf(productID);
                            DBqueries.removeFromWishlist(index, ProductDetailsActivity.this);

                            addToWishlisttBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));

                        } else {
                            addToWishlisttBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DBqueries.wishList.size()), productID);
                            addProduct.put("list_size", (long) (DBqueries.wishList.size() + 1));
                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                                    if (DBqueries.wishlistModelList.size() != 0) {
                                                        DBqueries.wishlistModelList.add(
                                                                new WishlistModel(productID, documentSnapshot.get("product_image_1").toString()
                                                                        , documentSnapshot.get("product_full_title").toString()
                                                                        , (long) documentSnapshot.get("free_coupens")
                                                                        , documentSnapshot.get("average_rating").toString()
                                                                        , (long) documentSnapshot.get("total_ratings")
                                                                        , documentSnapshot.get("product_price").toString()
                                                                        , documentSnapshot.get("cutted_price").toString()
                                                                        , (boolean) documentSnapshot.get("COD")
                                                                , inStock));
                                                    }
                                                    ALREDY_ADDED_TO_WISHLIST = true;
                                                    addToWishlisttBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
                                                    DBqueries.wishList.add(productID);
                                                    Toast.makeText(ProductDetailsActivity.this, "Added to wishlist successfully", Toast.LENGTH_SHORT).show();

                                    } else {
                                         addToWishlisttBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
//                                        addToWishlisttBtn.setEnabled(true);
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();

                                    }
                                    running_wishlist_query = false;
                                }
                            });

                        }
                    }
                }
            }
        });

        productsDetailsViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTablayout));
        productDetailsTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productImagesViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // rating layout

        rateNowContainer = findViewById(R.id.rate_now_container);

        for (int x =0;x<rateNowContainer.getChildCount();x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (starPosition != initialRating) {
                        if (!running_rating_query) {
                            running_rating_query = true;
                            setRating(starPosition);
                            Map<String, Object> updateRating = new HashMap<>();
                            if (DBqueries.myRatedIds.contains(productID)) {

                                TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
                                TextView finalRating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);

                                updateRating.put((initialRating +1) + "_star",Long.parseLong(oldRating.getText().toString()) -1);
                                updateRating.put(starPosition+1 + "_star",Long.parseLong(finalRating.getText().toString()) +1);
                                updateRating.put("average_rating", calculateAverageRating((long)starPosition - initialRating, true));

                            } else {

                                updateRating.put((starPosition + 1) + "_star", (long) documentSnapshot.get((starPosition + 1) + "_star") + 1);
                                updateRating.put("average_rating", calculateAverageRating((long)starPosition+1, false));
                                updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);

                                firebaseFirestore.collection("PRODUCTS").document(productID)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            final Map<String, Object> myRating = new HashMap<>();
                                            if (DBqueries.myRatedIds.contains(productID)) {
                                                myRating.put("rating_" + DBqueries.myRatedIds.indexOf(productID), (long) starPosition + 1);
                                            } else {
                                                myRating.put("list_size",(long) (DBqueries.myRatedIds.size())+1);
                                                myRating.put("product_ID_" + DBqueries.myRatedIds.size(), productID);
                                                myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starPosition + 1);
                                            }


                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBqueries.myRatedIds.contains(productID)) {
                                                            DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(productID), (long) starPosition + 1);

                                                            TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating + 1);  // -1
                                                            TextView finalRating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));

                                                        } else {

                                                            DBqueries.myRatedIds.add(productID);
                                                            DBqueries.myRating.add((long) starPosition + 1);

                                                            TextView rating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                            totalRatingMiniView.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ") ratings");
                                                            totalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                            totalRatingsFig.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));

                                                            Toast.makeText(ProductDetailsActivity.this, "Thankyou for rating", Toast.LENGTH_SHORT).show();
                                                        }

                                                        for (int x = 0; x < 5; x++) {
                                                            TextView ratingFigures = (TextView) ratingsNoContainer.getChildAt(x);

                                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                            int maxProgress = Integer.parseInt(totalRatingsFig.getText().toString());
                                                            progressBar.setMax(maxProgress);
                                                            progressBar.setProgress(Integer.parseInt(ratingFigures.getText().toString()));
                                                        }
                                                        initialRating = starPosition;
                                                        averageRating.setText(calculateAverageRating(0, true));
                                                        averageRatingMiniView.setText(calculateAverageRating(0, false));

                                                        if (DBqueries.wishList.contains(productID) && DBqueries.wishlistModelList.size() != 0) {
                                                            int index = DBqueries.wishList.indexOf(productID);
                                                            DBqueries.wishlistModelList.get(index).setRating(averageRating.getText().toString());
                                                            DBqueries.wishlistModelList.get(index).setTotalRatings(Long.parseLong(totalRatingsFig.getText().toString()));


                                                        }
                                                    } else {
                                                        setRating(initialRating);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });
                                        } else {
                                            running_rating_query = false;
                                            setRating(initialRating);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        }

                    }
                }
            });
        }

        //rating

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.fromCart = false;
                    loadingDialog.show();
                    productDetailsActivity = ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();

                    DeliveryActivity.cartItemModelList.add(new CartItemModel(documentSnapshot.getBoolean("COD"),CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
                            , documentSnapshot.get("product_title").toString()
                            , (long) documentSnapshot.get("free_coupens")
                            , documentSnapshot.get("product_price").toString()
                            , documentSnapshot.get("cutted_price").toString()
                            , (long) 1
                            , (long) documentSnapshot.get("offers_applied")
                            , (long) 0
                            , inStock
                            , (long) documentSnapshot.get("max_quantity")
                            , (long) documentSnapshot.get("stock_quantity")));
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                    if (DBqueries.addressModelList.size() == 0) {
                        DBqueries.loadAddresses(ProductDetailsActivity.this, loadingDialog, true);
                    } else {
                        loadingDialog.dismiss();
                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }
                }
            }
        });

//        addToCartBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (currentUser == null) {
//                    signInDialog.show();
//                } else {
//                    if (!running_cart_query) {
//                        running_cart_query = true;
//                        if (ALREDY_ADDED_TO_CART) {
//                            running_cart_query = false;
//                            Toast.makeText(ProductDetailsActivity.this, "Already added to cart", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Map<String, Object> addProduct = new HashMap<>();
//                            addProduct.put("product_ID_" + String.valueOf(DBqueries.cartList.size()), productID);
//                            addProduct.put("list_size", (long) (DBqueries.cartList.size() + 1));
//
//                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART")
//                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//
//                                        if (DBqueries.cartItemModelList.size() != 0) {
//                                            DBqueries.cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString()
//                                                    , documentSnapshot.get("product_title").toString()
//                                                    , (long) documentSnapshot.get("free_coupens")
//                                                    , documentSnapshot.get("product_price").toString()
//                                                    , documentSnapshot.get("cutted_price").toString()
//                                                    , (long) 1
//                                                    , (long) 0
//                                                    , (long) 0));
//                                        }
//                                        ALREDY_ADDED_TO_CART = true;
//                                        DBqueries.cartList.add(productID);
//                                        Toast.makeText(ProductDetailsActivity.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();
//                                        invalidateOptionsMenu();
//                                        running_cart_query = false;
//                                    }
//
//                                else {
////                                        addToWishlisttBtn.setEnabled(true);
//                                        running_cart_query = false;
//                                        String error = task.getException().getMessage();
//                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            });
//
//                        }
//                    }
//                }
//
//            }
//        });

        // Coupen Dialog


        coupenRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkCoupenPriceDialog.show();
            }
        });

//////// SignInDialog

        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);

        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });
//        signInDialog.show();


        /////sign dialog
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            coupenRedemptionLayout.setVisibility(View.GONE);
        } else {
            coupenRedemptionLayout.setVisibility(View.VISIBLE);
        }

        if (currentUser != null) {
            if (DBqueries.myRating.size() == 0) {
                DBqueries.loadRatingList(ProductDetailsActivity.this);
            }

            if (DBqueries.wishList.size() == 0) {
                DBqueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
//                                    DBqueries.loadRatingList(ProductDetailsActivity.this);
            }
            if (DBqueries.rewardModelList.size() == 0) {
                DBqueries.loadRewards(ProductDetailsActivity.this, loadingDialog, false);
            }
            if (DBqueries.cartList.size() != 0 && DBqueries.wishList.size() != 0 && DBqueries.rewardModelList.size() != 0) {
                loadingDialog.dismiss();
            }
        } else {
            loadingDialog.dismiss();
        }
        if (DBqueries.myRatedIds.contains(productID)) {
            int index = DBqueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBqueries.myRating.get(index))) -1;
            setRating(initialRating);
        }

        if (DBqueries.cartList.contains(productID)) {
            ALREDY_ADDED_TO_CART = true;

        } else {
            ALREDY_ADDED_TO_CART = false;
        }

        if (DBqueries.wishList.contains(productID)) {
            ALREDY_ADDED_TO_WISHLIST = true;
            addToWishlisttBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));

        } else {
            addToWishlisttBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
            ALREDY_ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();

    }

    private void showDialogRecyclerView() {
        if (coupenRecyclerView.getVisibility() == View.GONE) {
            coupenRecyclerView.setVisibility(View.VISIBLE);
            selectedCoupen.setVisibility(View.GONE);
        } else {
            coupenRecyclerView.setVisibility(View.GONE);
            selectedCoupen.setVisibility(View.VISIBLE);
        }

    }

    public static void setRating(int starPosition) {

            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if (x <= starPosition) {
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                }
            }

    }

    private String calculateAverageRating(long currentUserRating, boolean update) {
        double totalStars = 0;
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingsNoContainer.getChildAt(5-x);   // x-1 //x
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString())*x);
        }
        totalStars = totalStars +currentUserRating;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(totalRatingsFig.getText().toString())).substring(0,3);
        } else {
            return String.valueOf(totalStars / (Long.parseLong(totalRatingsFig.getText().toString()) + 1)).substring(0,3);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.seaech_and_cart_icon, menu);
        cartItem = menu.findItem(R.id.main_cart_icon);

            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.mipmap.cart_bad);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (currentUser != null) {

            if (DBqueries.cartList.size() == 0) {
                DBqueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
            } else {
                badgeCount.setVisibility(View.VISIBLE);
            }
            if (DBqueries.cartList.size() <99) {
                badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
            } else {
                badgeCount.setText("99");
            }
        }
            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                        showCart = true;
                        startActivity(cartIntent);
                    }
                }
            });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        } else if (id == R.id.main_search_icon) {
            if (fromSearch) {
                finish();
            } else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
            }
            return true;
        } else if (id == R.id.main_cart_icon) {
            if (currentUser == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                showCart = true;
                startActivity(cartIntent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }
}
