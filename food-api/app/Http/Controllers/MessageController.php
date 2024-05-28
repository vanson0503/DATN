<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Message;
use App\Models\Customer;
use Illuminate\Support\Facades\Response;
use Illuminate\Support\Facades\Validator;

class MessageController extends Controller
{
    public function getCustomerMessages($customerId)
    {
        $customer = Customer::find($customerId);

        // Lấy tin nhắn nơi khách hàng là người gửi hoặc là người nhận
        $messages = Message::where(function ($query) use ($customerId) {
            $query->where('sender_id', $customerId)
                ->where('sender_type', 'customer');
        })->orWhere(function ($query) use ($customerId) {
            $query->where('receiver_id', $customerId)
                ->where('sender_type', 'admin'); // Giả sử rằng chỉ có admin mới là người nhận
        })
            ->orderBy('created_at', 'desc')
            ->get();

        return
            response()->json($messages);
    }

    public function storeMessage(Request $request)
{
    // Validate request
    $validator = Validator::make($request->all(), [
        'sender_id' => 'required|integer',
        'receiver_id' => 'integer|nullable',
        'content' => 'required|string',
        'sender_type' => 'required|in:customer,admin'
    ]);

    // Check if validation fails
    if ($validator->fails()) {
        return response()->json(['errors' => $validator->errors()], 422);
    }

    // Determine receiver_id
    if ($request->receiver_id == -1) {
        $receiver_id = null;
    } else {
        $receiver_id = $request->receiver_id;
    }

    // Create a new message instance and save it to the database
    $message = new Message;
    $message->sender_id = $request->sender_id;
    $message->receiver_id = $receiver_id;
    $message->content = $request->content;
    $message->sender_type = $request->sender_type;
    $message->created_at = date('Y-m-d H:i:s');
    $message->updated_at = date('Y-m-d H:i:s');
    $message->save();

    // Return a successful response
    return response()->json(['message' => 'Message saved successfully'], 201);
}

    public function getCustomersWithLastMessage()
{
    // Get all customers with their latest message
    $customers = Customer::with(['messages' => function ($query) {
        $query->where(function ($q) {
            $q->where('sender_type', 'customer')
                ->orWhere('sender_type', 'admin');
        })
        ->orderBy('created_at', 'desc')
        ->limit(1);
    }])
    ->get()
    ->filter(function($customer) {
        return $customer->messages->isNotEmpty();
    })
    ->sortByDesc(function($customer) {
        return $customer->messages->first()->created_at;
    });

    // Transform the data to include the latest message details
    $customers = $customers->map(function ($customer) {
        $lastMessage = $customer->messages->first(); // The latest message loaded via the relationship
        return [
            'id' => $customer->id,
            'full_name' => $customer->full_name,
            'phone_number' => $customer->phone_number,
            'email' => $customer->email,
            'image_url' => $customer->image_url,
            'last_message' => [
                'content' => $lastMessage->content,
                'created_at' => $lastMessage->created_at,
                'sender_type' => $lastMessage->sender_type,
                'receiver_id' => $lastMessage->receiver_id,
                'sender_id' => $lastMessage->sender_id
            ]
        ];
    });

    return response()->json($customers->values()->all());
}




}
