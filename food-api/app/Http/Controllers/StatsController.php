<?php

namespace App\Http\Controllers;

use App\Models\Orders;
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

    public function monthlyRevenueWithChange()
    {
        // Lấy doanh thu theo tháng
        $revenues = DB::table('order_detail')
            ->join('orders', 'order_detail.orders_id', '=', 'orders.id')
            ->select(
                DB::raw("DATE_FORMAT(orders.created_time, '%Y-%m') as month"),
                DB::raw('SUM(order_detail.price * order_detail.quantity) as revenue')
            )
            ->groupBy('month')
            ->orderBy('month', 'asc')
            ->get();

        $result = [];
        $previousRevenue = null;

        foreach ($revenues as $revenue) {
            $change = null;

            if ($previousRevenue !== null) {
                $change = (($revenue->revenue - $previousRevenue) / $previousRevenue) * 100;
            }

            $result[] = [
                'month' => $revenue->month,
                'revenue' => $revenue->revenue,
                'change' => $change
            ];

            $previousRevenue = $revenue->revenue;
        }

        return response()->json($result);
    }
    public function monthlyCustomerSignUpsWithChange()
    {
        // Lấy số lượng tài khoản khách hàng được lập theo tháng
        $signUps = DB::table('customer')
            ->select(
                DB::raw("DATE_FORMAT(created_time, '%Y-%m') as month"),
                DB::raw('COUNT(*) as sign_ups')
            )
            ->groupBy('month')
            ->orderBy('month', 'asc')
            ->get();

        $result = [];
        $previousSignUps = null;

        foreach ($signUps as $signUp) {
            $change = null;

            if ($previousSignUps !== null) {
                $change = (($signUp->sign_ups - $previousSignUps) / $previousSignUps) * 100;
            }

            $result[] = [
                'month' => $signUp->month,
                'sign_ups' => $signUp->sign_ups,
                'change' => $change
            ];

            $previousSignUps = $signUp->sign_ups;
        }

        return response()->json($result);
    }

    public function monthlyCustomerStats()
    {
        // Lấy dữ liệu số lượng khách hàng mới theo tháng
        $stats = DB::table('customer')
            ->select(DB::raw("DATE_FORMAT(created_time, '%Y-%m') as month"), DB::raw('COUNT(*) as count'))
            ->groupBy('month')
            ->orderBy('month', 'asc')
            ->get();

        // Tính toán phần trăm thay đổi so với tháng trước
        $formattedData = $stats->map(function ($stat, $index) use ($stats) {
            $previousCount = $index > 0 ? $stats[$index - 1]->count : 0;
            $changePercent = $previousCount ? (($stat->count - $previousCount) / $previousCount) * 100 : 0;

            return [
                'month' => $stat->month,
                'count' => $stat->count,
                'change_percent' => round($changePercent, 2)
            ];
        });

        return response()->json($formattedData);
    }
    public function getMonthlySales()
    {
        $sales = Orders::selectRaw('DATE_FORMAT(created_time, "%Y-%m") as month, SUM(total) as revenue')
            ->groupBy('month')
            ->orderBy('month', 'asc')
            ->get();

        return response()->json($sales);
    }

    public function monthlyRevenueChange()
    {
        // Lấy dữ liệu doanh thu hàng tháng
        $stats = DB::table('order_detail')
            ->join('orders', 'order_detail.orders_id', '=', 'orders.id')
            ->select(DB::raw("DATE_FORMAT(orders.created_time, '%Y-%m') as month"), DB::raw('SUM(order_detail.price * order_detail.quantity) as revenue'))
            ->groupBy('month')
            ->orderBy('month', 'asc')
            ->get();

        // Tính toán phần trăm thay đổi so với tháng trước
        $formattedData = $stats->map(function ($stat, $index) use ($stats) {
            $previousRevenue = $index > 0 ? $stats[$index - 1]->revenue : 0;
            $changePercent = $previousRevenue ? (($stat->revenue - $previousRevenue) / $previousRevenue) * 100 : 0;

            return [
                'month' => $stat->month,
                'revenue' => $stat->revenue,
                'change_percent' => round($changePercent, 2)
            ];
        });

        return response()->json($formattedData);
    }

    public function monthlyProfitChange()
    {
        // Lấy dữ liệu lợi nhuận hàng tháng
        $stats = DB::table('order_detail')
            ->join('orders', 'order_detail.orders_id', '=', 'orders.id')
            ->select(DB::raw("DATE_FORMAT(orders.created_time, '%Y-%m') as month"), DB::raw('SUM(order_detail.price * order_detail.quantity) as profit'))
            ->groupBy('month')
            ->orderBy('month', 'asc')
            ->get();

        // Tính toán phần trăm thay đổi so với tháng trước
        $formattedData = $stats->map(function ($stat, $index) use ($stats) {
            $previousProfit = $index > 0 ? $stats[$index - 1]->profit : 0;
            $changePercent = $previousProfit ? (($stat->profit - $previousProfit) / $previousProfit) * 100 : 0;

            return [
                'month' => $stat->month,
                'profit' => $stat->profit,
                'change_percent' => round($changePercent, 2)
            ];
        });

        // Lấy dữ liệu tháng gần nhất
        $latestData = $formattedData->last();

        return response()->json($latestData);
    }

    public function productCountByCategory()
    {
        $stats = DB::table('product_category')
            ->join('category', 'product_category.category_id', '=', 'category.id')
            ->select('category.name', DB::raw('COUNT(product_category.product_id) as product_count'))
            ->groupBy('category.name')
            ->get();

        $totalProductCount = DB::table('product')->count();

        return response()->json([
            'stats' => $stats,
            'totalProductCount' => $totalProductCount
        ]);
    }

    public function soldProductCountByCategoryWithImage()
    {
        $stats = DB::table('order_detail')
            ->join('product', 'order_detail.product_id', '=', 'product.id')
            ->join('product_category', 'product.id', '=', 'product_category.product_id')
            ->join('category', 'product_category.category_id', '=', 'category.id')
            ->select('category.id', 'category.name', 'category.image_url', DB::raw('SUM(order_detail.quantity) as sold_count'))
            ->groupBy('category.id', 'category.name', 'category.image_url')
            ->get();

        return response()->json($stats);
    }


}
