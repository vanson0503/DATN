<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Category;


class CategoryController extends Controller
{
    public function index($id = null)
    {
        if ($id == null) {
            return response(json_encode(Category::get()),200);
        } else {
            if(Category::find($id)) {
                return response(json_encode(Category::find($id)),200);
            }
            return response(json_encode(['message'=>'Category not found!'],404));
        }
    }

    public function getCategoryByProductId($productId)
    {
        $category = Category::whereHas('product', function ($query) use ($productId) {
            $query->where('product.id', $productId);
        })->get();

        if ($category) {
            return response(json_encode($category), 200);
        }
        return response(json_encode(['message' => "Porduct not found!"]), 404);
    }

    public function create(Request $request)
    {
        try {
            $category = new Category;
            $category->name = $request->input('name');

            if ($request->hasFile('image')) {
                $imageName = $request->file('image')->getClientOriginalName(); // Lấy tên gốc của hình ảnh
                $imagePath = $request->file('image')->storeAs('public/category_images', $imageName); // Lưu trữ hình ảnh với tên cụ thể
                $category->image_url = $imageName; // Gán tên hình ảnh vào trường image_url
            }

            $category->save();

            return response()->json(['message' => 'Category added successfully'], 200);
        } catch (\Throwable $err) {
            return response()->json(['message' => 'Failed to add category: ' . $err->getMessage()], 500);
        }
    }

    public function update($id, Request $request)
    {
        try {
            $category = Category::find($id);
            if (!$category) {
                return response()->json(['message' => 'Category not found'], 404);
            }
            $category->name = $request->input('name');
            if ($request->hasFile('image')) {
                $imageName = $request->file('image')->getClientOriginalName();
                $imagePath = $request->file('image')->storeAs('public/category_images', $imageName);
                $category->image_url = $imageName;
            }
            $category->save();
            return response()->json(['message' => 'Category updated successfully'], 200);
        } catch (\Throwable $err) {
            return response()->json(['message' => 'Failed to update category: ' . $err->getMessage()], 500);
        }
    }

    public function delete($id)
    {
        try {
            $category = Category::find($id);
            if (!$category) {
                return response()->json(['message' => 'Category not found'], 404);
            }
            $category->delete();
            return response()->json(['message' => 'Category deleted successfully'], 200);
        } catch (\Throwable $err) {
            return response()->json(['message' => 'Failed to delete category: ' . $err->getMessage()], 500);
        }
    }

}
