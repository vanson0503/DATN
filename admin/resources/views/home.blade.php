@extends('../layout')

@section('content')

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<div class="container-xxl flex-grow-1 container-p-y">
    <div class="row">
        <div class="col-lg-8 mb-4 order-0">
            <div class="card">
                <div class="d-flex align-items-end row">
                    <div class="col-sm-7">
                        <div class="card-body">
                            <h5 class="card-title text-primary">Xử lý đơn hàng 📦</h5>
                            <p class="mb-4">
                                Bạn có <span id="pendingOrders" class="fw-bold">0</span> đơn hàng cần xửa lý.
                            </p>
                            <a href="#" class="btn btn-sm btn-outline-primary">Xem đơn hàng</a>
                        </div>
                    </div>
                    <div class="col-sm-5 text-center text-sm-left">
                        <div class="card-body pb-0 px-0 px-md-4">
                            <img src="{{ asset("assets/img/illustrations/add-product.png") }}" height="140"
                                alt="Order Processing" />
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <script>
            document.addEventListener('DOMContentLoaded', function () {
                fetch("http://localhost/food-api/public/api/stats/order-status")
                    .then(response => response.json())
                    .then(data => {
                        const pendingOrders = data.find(order => order.status === 'initialization');
                        const pendingOrdersCount = pendingOrders ? pendingOrders.total_orders : 0;
                        document.getElementById('pendingOrders').textContent = pendingOrdersCount;
                    })
                    .catch(error => console.error('Error fetching pending orders:', error));
            });
        </script>
        <div class="col-12 col-md-8 col-lg-4 order-3 order-md-2">
            <div class="row">
                <div class="col-12 mb-4">
                    <div class="card">
                        <div class="card-body">
                            <div class="card-title d-flex align-items-start justify-content-between">
                                <div class="avatar flex-shrink-0">
                                    <img src="{{asset("assets/img/icons/unicons/chart-success.png")}}"
                                        alt="chart success" class="rounded" />
                                </div>
                            </div>
                            <span class="fw-semibold d-block mb-1">Doanh thu với tháng trước</span>
                            <h3 id="profitAmount" class="card-title mb-2">0đ</h3>
                            <small id="profitChange" class="text-success fw-semibold"><i class="bx bx-up-arrow-alt"></i>
                                0%</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                fetch("http://localhost/food-api/public/api/stats/monthly-revenue-with-change")
                    .then(response => response.json())
                    .then(data => {
                        const latestMonthData = data[data.length - 1]; // Get the latest month data
                        const profitAmountElement = document.getElementById('profitAmount');
                        const profitChangeElement = document.getElementById('profitChange');

                        const profitAmount = parseFloat(latestMonthData.revenue); // Ensure the revenue is a number
                        const profitChange = latestMonthData.change;

                        // Format the revenue as VND
                        const formattedProfitAmount = new Intl.NumberFormat('vi-VN', {
                            style: 'currency',
                            currency: 'VND'
                        }).format(profitAmount);

                        profitAmountElement.textContent = formattedProfitAmount;

                        if (profitChange >= 0) {
                            profitChangeElement.innerHTML = `<i class="bx bx-up-arrow-alt"></i> ${profitChange.toFixed(2)}%`;
                            profitChangeElement.classList.add('text-success');
                            profitChangeElement.classList.remove('text-danger');
                        } else {
                            profitChangeElement.innerHTML = `<i class="bx bx-down-arrow-alt"></i> ${profitChange.toFixed(2)}%`;
                            profitChangeElement.classList.add('text-danger');
                            profitChangeElement.classList.remove('text-success');
                        }
                    })
                    .catch(error => console.error('Error fetching profit data:', error));
            });
        </script>
        <!-- Total Revenue -->
        <div class="col-12 col-lg-8 order-2 order-md-3 order-lg-2 mb-4">
            <div class="card">
                <div class="row row-bordered g-0">
                    <div class="col-md-12">
                        <h5 class="card-header m-0 me-2 pb-3">Tổng doanh thu</h5>
                        <canvas id="weeklyRevenueChart" width="1000" height="600"></canvas>
                    </div>

                </div>
            </div>
        </div>
        <!--/ Total Revenue -->
        <div class="col-12 col-md-8 col-lg-4 order-3 order-md-2">
            <div class="row">
                <div class="col-12 mb-4">
                    <div class="card">
                        <div class="card-body align-items-center">
                            <canvas id="productCategorySalesChart" width="600" height="400"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-lg-4 col-md-4 order-1">
            <div class="card h-100">
                <div class="card-header d-flex align-items-center justify-content-between pb-0">
                    <div class="card-title mb-0">
                        <h5 class="m-0 me-2">Số sản phẩm bán theo danh mục</h5>
                    </div>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <div class="d-flex flex-column align-items-center gap-1">
                            <span>Total Sold Products</span>
                        </div>
                        <div id="soldProductStatisticsChart"></div>
                    </div>
                    <ul class="p-0 m-0" id="soldCategoryList">
                        <!-- Category items will be injected here -->
                    </ul>
                </div>
            </div>
            
        </div>
        <div class="col-lg-8 col-md-4 order-1">
            <div class="card h-100">
                <div class="card-header d-flex align-items-center justify-content-between pb-0">
                    <div class="card-title mb-0">
                        <h5 class="m-0 me-2">Số sản phẩm bán theo danh mục</h5>
                    </div>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <div class="d-flex flex-column align-items-center gap-1">
                            <span>Total Sold Products</span>
                        </div>
                        <div id="soldProductStatisticsChart"></div>
                    </div>
                    <ul class="p-0 m-0" id="soldCategoryList">
                        <!-- Category items will be injected here -->
                    </ul>
                </div>
            </div>
    </div>

    <script>
        async function fetchSoldProductCountByCategoryWithImage() {
            try {
                const response = await fetch('http://localhost/food-api/public/api/stats/sold-product-count-by-category');
                const data = await response.json();

                const soldCategoryList = document.getElementById('soldCategoryList');
                soldCategoryList.innerHTML = '';

                data.forEach(item => {
                    const listItem = document.createElement('li');
                    listItem.classList.add('d-flex', 'mb-4', 'pb-1');

                    listItem.innerHTML = `
                        <div class="avatar flex-shrink-0 me-3">
                            <img src="http://localhost/food-api/storage/app/public/category_images/${item.image_url}" alt="${item.name}" class="rounded" style="width: 40px; height: 40px;">
                        </div>
                        <div class="d-flex w-100 flex-wrap align-items-center justify-content-between gap-2">
                            <div class="me-2">
                                <h6 class="mb-0">${item.name}</h6>
                            </div>
                            <div class="user-progress">
                                <small class="fw-semibold">Đã bán ${item.sold_count}</small>
                            </div>
                        </div>
                    `;
                    soldCategoryList.appendChild(listItem);
                });

            } catch (error) {
                console.error('Error fetching sold product count by category with image:', error);
            }
        }

        document.addEventListener('DOMContentLoaded', fetchSoldProductCountByCategoryWithImage);
    </script>

</div>



<script>
    document.addEventListener('DOMContentLoaded', function () {
        const ctx = document.getElementById('weeklyRevenueChart').getContext('2d');

        // Fetch month revenue data from API
        fetch('http://localhost/food-api/public/api/stats/monthly-revenue')
            .then(response => response.json())
            .then(data => {
                const labels = data.map(item => `Tháng ${item.month}`);
                const revenues = data.map(item => item.revenue);

                const chart = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Doanh thu theo tháng',
                            data: revenues,
                            borderColor: 'rgba(75, 192, 192, 1)',
                            backgroundColor: 'rgba(75, 192, 192, 0.2)',
                            fill: false,
                        }]
                    },
                    options: {
                        scales: {
                            x: {
                                display: true,
                                title: {
                                    display: true,
                                    text: 'Tháng'
                                }
                            },
                            y: {
                                display: true,
                                title: {
                                    display: true,
                                    text: 'Doanh thu'
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => console.error('Error fetching data:', error));
    });

    document.addEventListener('DOMContentLoaded', function () {
        const ctx = document.getElementById('productCategorySalesChart').getContext('2d');

        // Fetch product category sales data from API
        fetch('http://localhost/food-api/public/api/stats/category-sales')
            .then(response => response.json())
            .then(data => {
                const labels = data.map(item => item.name);
                const sales = data.map(item => item.quantity_sold);

                const chart = new Chart(ctx, {
                    type: 'polarArea',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Số sản phẩm bán theo danh mục',
                            data: sales,
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.5)',
                                'rgba(54, 162, 235, 0.5)',
                                'rgba(255, 206, 86, 0.5)',
                                'rgba(75, 192, 192, 0.5)',
                                'rgba(153, 102, 255, 0.5)',
                                'rgba(255, 159, 64, 0.5)'
                            ],
                            borderWidth: 1
                        }]
                    }
                });
            })
            .catch(error => console.error('Error fetching data:', error));
    });
</script>

@endsection