<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class OrderController extends Controller
{
    public function index(Request $request) {
        $page = $request->page ? $request->page : 1;
        return view("order.index", ['page' => $page]);
    }
    
}
