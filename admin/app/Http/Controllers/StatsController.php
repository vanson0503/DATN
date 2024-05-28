<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class StatsController extends Controller
{
    public function customerStatusStats()
    {
        $stats = DB::table('customer')
                    ->select('status', DB::raw('COUNT(*) as count'))
                    ->groupBy('status')
                    ->get();

        return response()->json($stats);
    }

    public function monthlyRevenue()
    {
        $stats = DB::table('order_detail')
                    ->join('orders', 'order_detail.orders_id', '=', 'orders.id')
                    ->select(DB::raw("DATE_FORMAT(orders.created_time, '%Y-%m') as month"), DB::raw('SUM(order_detail.price * order_detail.quantity) as revenue'))
                    ->groupBy('month')
                    ->get();

        return response()->json($stats);
    }

    public function categorySales()
    {
        $stats = DB::table('order_detail')
                    ->join('product', 'order_detail.product_id', '=', 'product.id')
                    ->join('product_category', 'product.id', '=', 'product_category.product_id')
                    ->join('category', 'product_category.category_id', '=', 'category.id')
                    ->select('category.name', DB::raw('SUM(order_detail.quantity) as quantity_sold'))
                    ->groupBy('category.name')
                    ->get();

        return response()->json($stats);
    }

    public function customerReviews()
    {
        $stats = DB::table('review')
                    ->join('customer', 'review.customer_id', '=', 'customer.id')
                    ->select('customer.full_name', DB::raw('COUNT(review.id) as review_count'))
                    ->groupBy('customer.full_name')
                    ->get();

        return response()->json($stats);
    }

    public function cartQuantities()
    {
        $stats = DB::table('cart')
                    ->join('customer', 'cart.customer_id', '=', 'customer.id')
                    ->select('customer.full_name', DB::raw('SUM(cart.quantity) as total_quantity'))
                    ->groupBy('customer.full_name')
                    ->get();

        return response()->json($stats);
    }
    
    public function orderStatusStats()
    {
        $stats = DB::table('orders')
                    ->select('status', DB::raw('COUNT(*) as total_orders'))
                    ->groupBy('status')
                    ->get();

        return response()->json($stats);
    }
}
