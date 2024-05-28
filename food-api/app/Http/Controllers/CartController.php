<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Cart;
use App\Models\Product;
use Illuminate\Support\Facades\DB;

class CartController extends Controller
{
    public function index(Request $request)
    {
        $customerId = $request->customer_id;
        if (!$customerId) {
            return response()->json(['message' => 'Customer ID is required'], 400);
        }

        // Lấy thông tin các mục trong giỏ hàng của khách hàng
        $cartItems = Cart::with('product.category', 'product.images') // Eager loading để giảm số lượng query
            ->where('customer_id', $customerId)
            ->get();

        $products = [];
        foreach ($cartItems as $cartItem) {
            $product = $cartItem->product; // Truy cập sản phẩm đã được load sẵn
            if ($product) {
                $quantity = $cartItem->quantity;
                $review = DB::table('review')
                    ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                    ->where('product_id', $product->id)
                    ->first();

                // Thêm các thông tin đánh giá và số lượng trong giỏ vào sản phẩm
                $product->total_reviews = $review->total_reviews;
                $product->average_rating = $review->average_rating;
                $product->cart_quantity = $quantity;

                $products[] = $product;
            }
        }

        return response()->json($products);
    }


    public function addToCart(Request $request)
    {

        $productId = $request->input('product_id');
        $customerId = $request->input('customer_id');
        $quantity = $request->input('quantity');
        $add = $request->input('add', "true"); 

        // Check if the required inputs are present
        if (!$productId || !$customerId || !$quantity) {
            return response()->json(['message' => 'Product ID, customer ID, and quantity are required'], 400);
        }

        // Check if the product exists
        $product = Product::find($productId);
        if (!$product) {
            return response()->json(['message' => 'Product not found'], 404);
        }


        // Handling addition or setting of quantity
        if ($add=="true") {

            // If add is true, increase the quantity by the specified amount
            $cartItem = Cart::updateOrCreate(
                ['product_id' => $productId, 'customer_id' => $customerId],
                ['quantity' => DB::raw('quantity + ' . $quantity)]
            );
        } else {
            // If add is false, set the quantity to the specified amount
            $cartItem = Cart::updateOrCreate(
                ['product_id' => $productId, 'customer_id' => $customerId],
                ['quantity' => $quantity]
            );
        }

        return response()->json(['message' => 'Product added to cart successfully'], 200);
    }


    public function removeFromCart(Request $request)
    {
        $productId = $request->input('product_id');
        $customerId = $request->input('customer_id');
        if (!$customerId) {
            return response()->json(['message' => 'Customer ID is required'], 400);
        }
        if ($productId) {
            Cart::where('product_id', $productId)->where('customer_id', $customerId)->delete();
            return response()->json(['message' => 'Product removed from cart successfully'], 200);
        } else {
            Cart::where('customer_id', $customerId)->delete();
            return response()->json(['message' => 'Cart cleared successfully'], 200);
        }
    }
}
