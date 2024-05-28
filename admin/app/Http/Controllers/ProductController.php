<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Product;
use App\Models\Category;
use Illuminate\Support\Facades\DB;

class ProductController extends Controller
{
    public function index()
    {
        return view("product.index");
    }

    public function create()
    {
        return view('product.create');
    }
    public function edit(string $id)
    {
        return view('product.edit', ['product_id'=> $id]);
    }

    public function destroy(Product $product)
    {
        $product->delete();

        return response()->json(['success' => true, 'message' => 'Category deleted successfully']);
    }
}