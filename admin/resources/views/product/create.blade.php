@extends('../layout')

@section('content')
<div class="container-xxl flex-grow-1 container-p-y">
    <h1>Thêm sản phẩm</h1>
    <form id="productForm" method="POST" enctype="multipart/form-data" class="product-form">
        <label for="name" class="form-label">Name:</label><br>
        <input type="text" id="name" name="name" class="form-control"><br><br>

        <label for="description" class="form-label">Description:</label><br>
        <textarea id="description" name="description" class="form-control"></textarea><br><br>

        <label for="ingredient" class="form-label">Ingredient:</label><br>
        <textarea id="ingredient" name="ingredient" class="form-control"></textarea><br><br>

        <label for="calo" class="form-label">Calories:</label><br>
        <input type="number" id="calo" name="calo" class="form-control"><br><br>

        <label for="quantity" class="form-label">Quantity:</label><br>
        <input type="number" id="quantity" name="quantity" class="form-control"><br><br>

        <label for="price" class="form-label">Price:</label><br>
        <input type="number" id="price" name="price" class="form-control"><br><br>

        <label for="categories" class="form-label">Categories:</label><br>
        <div id="categoriesContainer" class="category-container"></div><br>

        <label for="image" class="form-label">Image:</label><br>
        <input type="file" id="images" name="images[]" multiple class="form-control"><br><br>

        <input type="submit" value="Submit" class="btn btn-primary">
    </form>

</div>


<script>
    // Fetch categories from API
    fetch('http://localhost/food-api/public/api/category')
        .then(response => response.json())
        .then(categories => {
            const categoriesContainer = document.getElementById('categoriesContainer');
            categories.forEach(category => {
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.name = 'category[]';
                checkbox.value = category.id;

                const label = document.createElement('label');
                label.htmlFor = 'category' + category.id;
                label.textContent = category.name;

                categoriesContainer.appendChild(checkbox);
                categoriesContainer.appendChild(label);
                categoriesContainer.appendChild(document.createElement('br'));
            });
        });

    document.getElementById('productForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const formData = new FormData(this);
        fetch('http://localhost/food-api/public/api/products', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    return response.text();
                } else {
                    throw new Error('Network response was not ok.');
                }
            })
            .then(htmlData => {
                // Show success toast
                showToast('Success', 'Product added successfully', 'bg-success');
                console.log(htmlData); // In ra dữ liệu HTML
                document.getElementById('productForm').reset();
            })
            .catch(error => {
                // Show error toast
                showToast('Error', 'Failed to add product', 'bg-danger');
                console.error('Error:', error);
            });
    });
    //bs-toast toast toast-placement-ex m-2 fade bg-danger top-0 start-50 translate-middle-x show

    // Function to show toast dynamically


</script>



@endsection