@extends('../layout')

@section('content')
<div class="container-xxl flex-grow-1 container-p-y">
    <nav>
        <div class="navbar-nav-right d-flex align-items-center" id="navbar-collapse" style="z-index:-1 !important">
            <!-- Search -->
            <div class="navbar-nav align-items-center">
                <a href="{{ route('product.create') }}" class="btn rounded-pill btn-success">
                    <div data-i18n="Analytics">Thêm sản phẩm</div>
                </a>
            </div>
            <!-- /Search -->
            <div class="navbar-nav flex-row align-items-center ms-auto">
                <div class="nav-item d-flex align-items-center">
                    <i class="bx bx-search fs-4 lh-0"></i>
                    <input type="text" id="searchInput" class="form-control border-0 shadow-none"
                        placeholder="Search..." aria-label="Search..." onkeyup="filterProducts()" />
                </div>
            </div>
        </div>
    </nav>

    <div class="card">

        <h5 class="card-header">Product table</h5>
        <div id="productTable" class="table-responsive">
            <table id="productTableBody" class="table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Calories</th>
                        <th>Quantity</th>
                        <th>Price</th>
                        <th>Ingredient</th>
                        <th>Image</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="productList">
                    <!-- Products will be displayed here -->
                </tbody>
            </table>
        </div>
    </div>
</div>


<!-- Bootstrap JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<script>


    var baseEditUrl = "{{ route('product.edit', ['id']) }}";


    document.addEventListener("DOMContentLoaded", function () {
        // Fetch data from API
        fetch('http://localhost/food-api/public/api/products')
            .then(response => response.json())
            .then(data => {
                displayProducts(data);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
    function displayProducts(products) {
        const productList = document.getElementById('productList');
        products.forEach(product => {
            const id = product.id
            const row = document.createElement('tr');
            // Create cells and populate data for each product
            const nameCell = document.createElement('td');
            nameCell.textContent = product.name;

            const descriptionCell = document.createElement('td');
            descriptionCell.textContent = product.description;

            const caloCell = document.createElement('td');
            caloCell.textContent = product.calo;

            const quantityCell = document.createElement('td');
            quantityCell.textContent = product.quantity;

            const priceCell = document.createElement('td');
            priceCell.textContent = product.price;

            const ingredientCell = document.createElement('td');
            ingredientCell.textContent = product.ingredient;

            const imageCell = document.createElement('td');
            const image = document.createElement('img');
            let src = "";
            if (product.images && product.images.length > 0) {
                let imageUrl = product.images[0].imgurl;
                if (!imageUrl.startsWith('https://')) {
                    imageUrl = "http://localhost/food-api/storage/app/public/product_images/" + imageUrl;
                }
                src = imageUrl;
            }

            image.src = src;
            image.style.width = '100px';
            imageCell.appendChild(image);

            const actionsCell = document.createElement('td');

            // Create dropdown menu for actions
            const dropdownDiv = document.createElement('div');
            dropdownDiv.classList.add('dropdown');

            const dropdownToggleBtn = document.createElement('button');
            dropdownToggleBtn.type = 'button';
            dropdownToggleBtn.classList.add('btn', 'p-0', 'dropdown-toggle', 'hide-arrow');
            dropdownToggleBtn.setAttribute('data-bs-toggle', 'dropdown');
            dropdownToggleBtn.innerHTML = '<i class="bx bx-dots-vertical-rounded"></i>';

            const dropdownMenu = document.createElement('div');
            dropdownMenu.classList.add('dropdown-menu');

            const editLink = document.createElement('a');
            editLink.classList.add('dropdown-item');
            editLink.href = baseEditUrl.replace('id', id);
            editLink.innerHTML = '<i class="bx bx-edit-alt me-2"></i> Edit';

            const deleteLink = document.createElement('a');
            deleteLink.classList.add('dropdown-item');
            deleteLink.href = 'javascript:void(0);';
            deleteLink.innerHTML = '<i class="bx bx-trash me-2"></i> Delete';
            deleteLink.addEventListener('click', function () {
                if (confirm('Are you sure you want to delete this product?')) {
                    fetch('http://localhost/food-api/public/api/products/' + product.id, {
                        method: 'DELETE'
                    })
                        .then(response => {
                            if (response.ok) {
                                showToast("Xóa sản phẩm", "Xóa sản phẩm thành công!", "bg-success");
                                product.row.remove(); // Remove the row from the table
                            } else {
                                showToast("Xóa sản phẩm", "Xóa sản phẩm thất bại!", "bg-danger");
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('Error deleting product.');
                        });
                }
            });

            dropdownMenu.appendChild(editLink);
            dropdownMenu.appendChild(deleteLink);

            dropdownDiv.appendChild(dropdownToggleBtn);
            dropdownDiv.appendChild(dropdownMenu);

            actionsCell.appendChild(dropdownDiv);

            // Store the row for each product
            product.row = row;

            // Append cells to the row
            row.appendChild(nameCell);
            row.appendChild(descriptionCell);
            row.appendChild(caloCell);
            row.appendChild(quantityCell);
            row.appendChild(priceCell);
            row.appendChild(ingredientCell);
            row.appendChild(imageCell);
            row.appendChild(actionsCell);

            // Append the row to the table
            productList.appendChild(row);
        });
    }

    function filterProducts() {
        const input = document.getElementById('searchInput');
        const filter = input.value.toUpperCase();
        const table = document.getElementById('productTableBody');
        const rows = table.getElementsByTagName('tr');

        // Loop through all table rows, and hide those who don't match the search query
        for (let i = 0; i < rows.length; i++) {
            const nameCell = rows[i].getElementsByTagName('td')[0];
            if (nameCell) {
                const txtValue = nameCell.textContent || nameCell.innerText;
                if (txtValue.toUpperCase().indexOf(filter) > -1) {
                    rows[i].style.display = '';
                } else {
                    rows[i].style.display = 'none';
                }
            }
        }
    }

</script>
@endsection