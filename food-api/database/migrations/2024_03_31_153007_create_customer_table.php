<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('customer', function (Blueprint $table) {
            $table->id();
            $table->string('full_name', 100);
            $table->string('phone_number', 20)->nullable();
            $table->string('email', 100)->nullable();
            $table->string('password', 100);
            $table->enum('status', ['active', 'inactive'])->default('active');
            $table->string('image_url', 100)->nullable();
            $table->string('social_name', 255)->nullable();
            $table->string('verify_code', 255)->nullable();
            $table->timestamp('created_time')->default(DB::raw('CURRENT_TIMESTAMP'));
            $table->timestamp('updated_time')->default(DB::raw('CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP'));
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('customer');
    }
};
