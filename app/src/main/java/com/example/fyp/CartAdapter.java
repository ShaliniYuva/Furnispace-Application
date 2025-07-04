package com.example.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartList;
    private Context context;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(List<Product> cartList, CartUpdateListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = cartList.get(position);

        holder.productTitle.setText(product.title);
        holder.productPrice.setText(product.price);
        holder.textQuantity.setText(String.valueOf(product.quantity > 0 ? product.quantity : 1));

        // Load image using imageName
        int imageResId = context.getResources().getIdentifier(product.imageName, "drawable", context.getPackageName());
        holder.productImage.setImageResource(imageResId);

        // Increase quantity
        holder.btnIncrease.setOnClickListener(v -> {
            product.quantity++;
            holder.textQuantity.setText(String.valueOf(product.quantity));
            updateQuantityInFirebase(product);
            notifyItemChanged(position);
            listener.onCartUpdated();
        });

        // Decrease quantity
        holder.btnDecrease.setOnClickListener(v -> {
            if (product.quantity > 1) {
                product.quantity--;
                holder.textQuantity.setText(String.valueOf(product.quantity));
                updateQuantityInFirebase(product);
                notifyItemChanged(position);
                listener.onCartUpdated();
            }
        });


        // Delete item
        holder.btnDelete.setOnClickListener(v -> {
            removeFromFirebaseCart(product, holder.getAdapterPosition(), v);
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    private void showStyledToast(String message) {
        View layout = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);
        TextView text = layout.findViewById(R.id.textToastMessage);
        text.setText(message);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void updateQuantityInFirebase(Product product) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("cart")
                .child(uid)
                .child(product.title)
                .setValue(product);
    }

    private void removeFromFirebaseCart(Product product, int position, View view) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance("https://arapps-final-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("cart")
                .child(uid)
                .child(product.title)
                .removeValue()
                .addOnSuccessListener(unused -> {
                    cartList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartList.size());
                    showStyledToast("Removed from cart");
                    listener.onCartUpdated();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show());
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productPrice, textQuantity;
        ImageView productImage;
        ImageButton btnIncrease, btnDecrease, btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.textCartTitle);
            productPrice = itemView.findViewById(R.id.textCartPrice);
            productImage = itemView.findViewById(R.id.imageCartProduct);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
