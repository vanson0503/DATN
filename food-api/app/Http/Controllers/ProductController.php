<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Product;
use App\Models\Category;
use App\Models\OrderDetail;
use Illuminate\Support\Facades\DB;

class ProductController extends Controller
{
    public function index($id = null)
    {
        if ($id == null) {
            // Get all products with categories and images, sorted by created time
            $products = Product::with(['category', 'images'])
                ->orderBy('created_time', 'desc')
                ->get();

            // Fetch the review data and total sold for each product
            $products->map(function ($product) {
                // Fetch and attach review data
                $review = DB::table('review')
                    ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                    ->where('product_id', $product->id)
                    ->first();
                $product->total_reviews = $review->total_reviews;
                $product->average_rating = $review->average_rating;

                // Fetch and attach total sold data
                $totalSold = OrderDetail::where('product_id', $product->id)
                    ->join('orders', 'orders.id', '=', 'order_detail.orders_id')
                    ->where('orders.status', 'completed')
                    ->sum('order_detail.quantity');
                $product->total_sold = $totalSold;

                return $product;
            });

            return response()->json($products);
        } else {
            // Fetch a single product with the specified ID
            $product = Product::with(['category', 'images'])
                ->find($id);

            if ($product) {
                // Fetch and attach review data
                $review = DB::table('review')
                    ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                    ->where('product_id', $product->id)
                    ->first();
                $product->total_reviews = $review->total_reviews;
                $product->average_rating = $review->average_rating;

                // Fetch and attach total sold data
                $totalSold = OrderDetail::where('product_id', $product->id)
                    ->join('orders', 'orders.id', '=', 'order_detail.orders_id')
                    ->where('orders.status', 'completed')
                    ->sum('order_detail.quantity');
                $product->total_sold = $totalSold;
            }

            return response()->json($product);
        }
    }


    public function productsByCategoryId($categoryId)
    {
        // Check if the category ID is valid
        $category = Category::find($categoryId);
        if (!$category) {
            return response()->json(['message' => 'Category not found'], 404);
        }

        $products = $category->product()
            ->with('category', 'images')
            ->orderBy('created_time', 'desc')
            ->get();


        foreach ($products as $product) {
            // Fetch review data
            $review = DB::table('review')
                ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                ->where('product_id', $product->id)
                ->first();

            $product->total_reviews = $review->total_reviews;
            $product->average_rating = $review->average_rating;

            // Fetch total sold data
            $totalSold = DB::table('order_detail')
                ->join('orders', 'orders.id', '=', 'order_detail.orders_id')
                ->where('orders.status', 'completed')
                ->where('order_detail.product_id', $product->id)
                ->sum('order_detail.quantity');

            $product->total_sold = $totalSold;
        }

        return response()->json($products);
    }

    public function topRate(Request $request)
    {
        $limit = $request->limit ?? 16; // Set a default limit if none is provided in the request
        $products = Product::with('category', 'images')
            ->orderBy('created_time', 'desc')
            ->get();
        foreach ($products as $product) {
            // Lấy thông tin đánh giá cho sản phẩm hiện tại
            $review = DB::table('review')
                ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                ->where('product_id', $product->id)
                ->first();

            // Thêm thông tin đánh giá vào đối tượng sản phẩm
            $product->total_reviews = $review->total_reviews;
            $product->average_rating = $review->average_rating;
            // Fetch total sold data
            $totalSold = DB::table('order_detail')
                ->join('orders', 'orders.id', '=', 'order_detail.orders_id')
                ->where('orders.status', 'completed')
                ->where('order_detail.product_id', $product->id)
                ->sum('order_detail.quantity');

            $product->total_sold = $totalSold;
        }

        $sortedProducts = collect($products)->sortByDesc('average_rating')->take($limit);

        // Return the sorted and limited list of products as JSON
        return response()->json($sortedProducts->values()->all());
    }


    public function topSale(Request $request)
    {
        $limit = $request->limit ?? 16;

        // First, get the IDs of the top-selling products
        $topSellingProducts = Product::join('order_detail', 'order_detail.product_id', '=', 'product.id')
            ->join('orders', 'orders.id', '=', 'order_detail.orders_id')
            ->where('orders.status', 'completed')
            ->select('product.id', DB::raw('SUM(order_detail.quantity) as total_sold'))
            ->groupBy('product.id')
            ->orderByRaw('SUM(order_detail.quantity) DESC')
            ->take($limit)
            ->get();

        // Extract only the product IDs
        $productIds = $topSellingProducts->pluck('id');

        // Fetch detailed product information based on these IDs
        $detailedProducts = Product::whereIn('id', $productIds)
            ->with(['category', 'images'])
            ->get()
            ->map(function ($product) {
                // Optionally add any additional data processing here, such as attaching reviews
                $review = DB::table('review')
                    ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                    ->where('product_id', $product->id)
                    ->first();
                $product->total_reviews = $review->total_reviews;
                $product->average_rating = $review->average_rating;
                return $product;
            });

        // Combine the sales data with detailed product information
        $result = $topSellingProducts->map(function ($sale) use ($detailedProducts) {
            $detail = $detailedProducts->firstWhere('id', $sale->id);
            return (object) array_merge((array) $sale->toArray(), (array) $detail->toArray());
        });

        return response()->json($result);
    }




    public function least($limit = null)
    {
        if ($limit == null) {
            $products = Product::with('category', 'images')
                ->orderBy('product.created_time', 'asc')
                ->take(16)
                ->get();
        } else {
            $products = Product::with('category', 'images')
                ->orderBy('product.created_time', 'desc')
                ->take($limit)
                ->get();
        }
        foreach ($products as $product) {
            $review = DB::table('review')
                ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                ->where('product_id', $product->id)
                ->first();
            $product->total_reviews = $review->total_reviews;
            $product->average_rating = $review->average_rating;
        }
        // var_dump($products);
        return response()->json($products);
    }

    public function searchByName(Request $request)
    {
        $keyword = $request->keyword;
        if ($keyword) {
            $products = Product::with('category', 'images')
                ->where('name', 'like', "%$keyword%")
                ->orderBy('created_time', 'desc')
                ->get();
            foreach ($products as $product) {
                $review = DB::table('review')
                    ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                    ->where('product_id', $product->id)
                    ->first();

                $product->total_reviews = $review->total_reviews;
                $product->average_rating = $review->average_rating;
            }
            return response()->json($products);
        }
        return response()->json(['message' => 'Product not found'], 404);
    }

    public function searchByPriceRange(Request $request)
    {
        // Lấy giá trị của tham số từ truy vấn
        $minPrice = $request->query('min_price');
        $maxPrice = $request->query('max_price');
        // Tìm kiếm sản phẩm trong khoảng giá từ $minPrice đến $maxPrice
        $products = Product::with('category', 'images')
            ->whereBetween('price', [$minPrice, $maxPrice])
            ->orderBy('created_time', 'desc')
            ->get();
        foreach ($products as $product) {
            // Lấy thông tin đánh giá cho sản phẩm hiện tại
            $review = DB::table('review')
                ->select(DB::raw('COALESCE(COUNT(*), 0) as total_reviews, COALESCE(AVG(rate), 0) as average_rating'))
                ->where('product_id', $product->id)
                ->first();

            // Thêm thông tin đánh giá vào đối tượng sản phẩm
            $product->total_reviews = $review->total_reviews;
            $product->average_rating = $review->average_rating;
        }
        return response()->json($products);
    }



    public function create(Request $request)
    {
        try {
            $product = new Product;

            $product->name = $request->input('name');
            $product->description = $request->input('description');
            $product->ingredient = $request->input('ingredient');
            $product->calo = $request->input('calo');
            $product->quantity = $request->input('quantity');
            $product->price = $request->input('price');

            $product->save();

            if ($request->has('category')) {
                $product->category()->attach($request->input('category'));
            }

            if ($request->hasFile('images')) {

                foreach ($request->file('images') as $image) {
                    $imagePath = $image->store('public/product_images');
                    // Lấy tên tệp từ đường dẫn đã lưu
                    $imageName = basename($imagePath);
                    $product->images()->create(['imgurl' => $imageName]);
                }
            }
            return response()->json(['message' => 'Product added successfully'], 200);
        } catch (\Throwable $err) {
            return response()->json(['message' => 'Failed to add product: ' . $err->getMessage()], 500);
        }
    }

    public function update($id, Request $request)
    {
        try {
            $product = Product::findOrFail($id);
            $product->update([
                'name' => $request->input('name'),
                'description' => $request->input('description'),
                'ingredient' => $request->input('ingredient'),
                'calo' => $request->input('calo'),
                'quantity' => $request->input('quantity'),
                'price' => $request->input('price')
            ]);

            // Xóa hình ảnh cũ của sản phẩm
            $product->images()->delete();

            // Lưu hình ảnh cũ
            $oldImages = $request->input('oldImages');
            if (!empty($oldImages)) {
                foreach ($oldImages as $oldImage) {
                    $product->images()->create(['imgurl' => $oldImage]);
                }
            }

            // Lưu hình ảnh mới
            if ($request->hasFile('images')) {
                foreach ($request->file('images') as $image) {
                    $imagePath = $image->store('public/product_images');
                    // Lấy tên tệp từ đường dẫn đã lưu
                    $imageName = basename($imagePath);
                    $product->images()->create(['imgurl' => $imageName]);
                }
            }

            // Cập nhật danh mục sản phẩm
            if ($request->has('category')) {
                $categoryIds = $request->input('category');
                $product->category()->sync($categoryIds); // Sử dụng sync để cập nhật liên kết
            }

            return response()->json(['message' => 'Product updated successfully'], 200);
        } catch (\Throwable $err) {
            return response()->json(['message' => 'Failed to update product: ' . $err->getMessage()], 500);
        }
    }

    public function delete($id)
    {
        try {
            $product = Product::find($id);

            if ($product) {
                $product->delete();
                return response()->json(['message' => 'Product deleted successfully'], 200);
            } else {
                return response()->json(['message' => 'Product not found'], 404);
            }
        } catch (\Throwable $err) {
            return response()->json(['message' => 'Failed to add product: ' . $err->getMessage()], 500);
        }
    }

    public function search(Request $request)
    {
        $query = Product::query()->with('category', 'images');

        // Filter by category ID(s)
        if ($request->has('category_ids')) {
            $categoryIds = $request->input('category_ids');
            $query->whereIn('category_id', $categoryIds);
        }

        // Filter by price range
        if ($request->has('min_price') || $request->has('max_price')) {
            if ($request->has('min_price')) {
                $minPrice = $request->input('min_price');
                $query->where('price', '>=', $minPrice);
            }

            if ($request->has('max_price')) {
                $maxPrice = $request->input('max_price');
                $query->where('price', '<=', $maxPrice);
            }
        }

        // Filter by name
        if ($request->has('keyword')) {
            $keyword = $request->input('keyword');
            $query->where('name', 'like', "%$keyword%");
        }

        // Filter by min rating
        if ($request->has('min_rating')) {
            $minRating = $request->input('min_rating');
            $query->whereHas('reviews', function ($q) use ($minRating) {
                $q->select(DB::raw('AVG(rate) as average_rating'))
                    ->havingRaw('average_rating >= ?', [$minRating]);
            });
        }

        // Sort by newest
        if ($request->has('sort_by') && $request->input('sort_by') == 'newest') {
            $query->orderBy('created_time', 'desc');
        }

        $products = $query->get();

        // Attach additional information (total reviews, average rating, total sold) to each product
        foreach ($products as $product) {
            $product->total_reviews = $product->reviews()->count();
            $product->average_rating = $product->reviews()->avg('rate');
            $product->total_sold = $product->orderDetails()->join('orders', 'orders.id', '=', 'order_detail.orders_id')
                ->where('orders.status', 'completed')
                ->sum('order_detail.quantity');
        }


        return response()->json($products);
    }


}