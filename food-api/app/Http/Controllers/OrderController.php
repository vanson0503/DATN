<?php

namespace App\Http\Controllers;

use App\Models\Orders;
use App\Models\OrderDetail;
use App\Models\Cart;
use App\Models\Location;
use Illuminate\Support\Facades\DB;
use Illuminate\Http\Request;


class OrderController extends Controller
{
    public function index(Request $request)
    {
        $page = $request->input('page', 1);
        $limit = $request->input('limit', 10);
        $offset = ($page - 1) * $limit;

        $orders = Orders::orderBy('created_time', 'desc')
            ->offset($offset)
            ->limit($limit)
            ->get();

        $total = Orders::count();
        $totalPages = ceil($total / $limit);

        // Tính trang trước và trang sau
        $prevPage = $page > 1 ? $page - 1 : null;
        $nextPage = $page < $totalPages ? $page + 1 : null;

        // Trả về dữ liệu dưới dạng JSON cùng với thông tin phân trang
        return response()->json([
            'data' => $orders,
            'total' => $total,
            'current_page' => $page,
            'limit' => $limit,
            'total_pages' => $totalPages,
            'prev_page' => $prevPage,
            'next_page' => $nextPage
        ]);
    }

    public function show($id)
    {
        $orders = Orders::with([
            'details.product.category',   // Load product category
            'details.product.images'      // Load product images
        ])->find( $id );


        return response()->json($orders);
    }

    public function getCustomerOrders($customerId)
    {
        // Fetch all orders for the given customer ID with related product details
        $orders = Orders::with([
            'details.product.category',   // Load product category
            'details.product.images'      // Load product images
        ])->where('customer_id', $customerId)->orderBy('created_time', 'desc')->get();


        return response()->json($orders);
    }




    public function createOrder(Request $request)
    {
        $customerId = $request->input('customer_id');
        $locationId = $request->input('location_id');
        $paymentMethod = $request->input('payment');
        $note = $request->input('note');
        $paymentStatus = $request->input('payment_status') ;

        $request->validate([
            'customer_id' => 'required|exists:customer,id',
            'location_id' => 'required|exists:location,id',
            'payment' => 'required|string',
            'note' => 'string|nullable',
        ]);

        // Begin a transaction
        DB::beginTransaction();
        try {
            // Fetch location details
            $location = Location::findOrFail($locationId);
            
            // Create new order
            $order = new Orders();
            $order->customer_id = $customerId;
            $order->name = $location->name;
            $order->phone_number = $location->phone_number;
            $order->address = $location->address;
            $order->note = $note;
            $order->payment = $paymentMethod;
            if($paymentStatus){
                $order->payment_status = 'completed';
            }else{
                $order->payment_status = 'pending';
            }
             // Set to pending until payment is confirmed
            $order->status = 'pending'; // Example status
            $order->save();

            // Fetch cart items
            $cartItems = Cart::where('customer_id', $customerId)->get();

            if ($cartItems->isEmpty()) {
                return response()->json(['message' => 'Cannot place an order with an empty cart'], 400);
            }

            foreach ($cartItems as $item) {
                // Create order details for each item
                $orderDetail = new OrderDetail();
                $orderDetail->orders_id = $order->id;
                $orderDetail->product_id = $item->product_id;
                $orderDetail->quantity = $item->quantity;
                $orderDetail->price = $item->product->price * (1 - $item->product->discount / 100); // Assuming discount is a percentage
                $orderDetail->save();
            }

            // Clear the cart after processing
            Cart::where('customer_id', $customerId)->delete();

            // Commit the transaction
            DB::commit();

            return response()->json(['message' => 'Order placed successfully!'], 200);
        } catch (\Exception $e) {
            // An error occurred; cancel the transaction...
            DB::rollback();

            // and return an error message
            return response()->json(['message' => 'Failed to place order', 'error' => $e->getMessage()], 500);
        }
    }
}
