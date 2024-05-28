<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

class ChatController extends Controller
{
    public function index(){
        return view("chat.index");
    }

    public function chatdetail(){
        return view("chat.chatdetail");
    }
}
