<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Category;

class CategoryController extends Controller
{
    public function index()
    {
        $categories = Category::all(); // Lấy danh sách tất cả các categories từ database
        return view('category.index', ['categories' => $categories]);
    }
}
