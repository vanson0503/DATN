<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Customer extends Model
{
    use HasFactory;
    protected $table = "customer";

    public $timestamps = false;

    public function orders()
    {
        return $this->hasMany(Orders::class);
    }

    public function messages()
    {
        return $this->hasMany(Message::class, 'sender_id')->where('sender_type', 'customer');
    }

    public function getLastMessageAttribute()
    {
        return Message::where(function ($query) {
            $query->where('sender_id', $this->id)->where('sender_type', 'customer');
        })->orWhere(function ($query) {
            $query->where('receiver_id', $this->id)->where('sender_type', 'admin');
        })->latest()->first();
    }


}
