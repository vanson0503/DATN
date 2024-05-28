@extends('../layout')

@section('content')
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

<style>
    .table td,
    .table th {
        vertical-align: middle;
    }

    .action-icon {
        cursor: pointer;
        color: #007bff;
        /* Bootstrap primary color */
        margin-right: 5px;
    }

    .action-icon:hover {
        color: #0056b3;
        /* Darker shade for hover effect */
    }

    .btn-add {
        margin-bottom: 15px;
    }
</style>
<div class="container-xxl flex-grow-1 container-p-y">
    <div class="container">
        <h3 class="my-4">Category List</h3>
        <button class="btn btn-success btn-add" data-bs-toggle="modal" data-bs-target="#addCategoryModal">
            <i class="fas fa-plus"></i> Add Category
        </button>
        <table class="table table-bordered table-hover">
            <thead class="thead-light">
                <tr>
                    <th>Image</th>
                    <th>Name</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="categoryList">
                <!-- Categories will be loaded here dynamically -->
            </tbody>
        </table>
    </div>


    <div class="modal fade" id="addCategoryModal" tabindex="-1" aria-labelledby="addCategoryModalLabel"
        aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addCategoryModalLabel">Add category</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="addCategoryForm" enctype="multipart/form-data">
                        <label for="name">Category Name:</label><br>
                        <input type="text" id="name" class="form-control" name="name" required><br><br>

                        <label for="image">Category Image:</label><br>
                        <input type="file" class="form-control" id="image" name="image" accept="image/*"
                            required><br><br>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" onclick="addCategory()">Add Category</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="editCategoryModal" tabindex="-1" aria-labelledby="editCategoryModalLabel"
        aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editCategoryModalLabel">Edit category</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="editCategoryForm" enctype="multipart/form-data">
                        <input type="hidden" id="id" class="form-control" name="id" required><br><br>
                        <label for="name">Category Name:</label><br>
                        <input type="text" id="editName" class="form-control" name="name" required><br>
                        <label for="name">Image:</label><br>
                        <img style="width:200px" id="oldImage" />
                        <br /><br>
                        <label for="image">Category Image:</label><br>
                        <input type="file" class="form-control" id="editImage" name="image" accept="image/*"><br><br>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" onclick="submitEditCategory()">Edit Category</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade" id="smallModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-sm" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel2">Category</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <label for="nameSmall" class="form-label">Bạn có chắc chắn muốn xóa?</label>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        Close
                    </button>
                    <button type="button" class="btn btn-danger" onClick="confirmDelete()">Delete</button>
                </div>
            </div>
        </div>
    </div>
</div>


<!-- Include jQuery and Bootstrap JS -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
    var currentDeletingCategoryId = null;
    function addCategory() {

        var form = document.getElementById("addCategoryForm");

        // Check form validity
        if (!form.checkValidity()) {
            // If the form is not valid, display an error message
            showToast("Lỗi", "Vui lòng điền đầy đủ thông tin!", "bg-warning");
            form.classList.add('was-validated'); // Bootstrap class to handle form validation feedback
            return; // Stop the function
        }

        // Lấy dữ liệu từ form
        var formData = new FormData(document.getElementById("addCategoryForm"));


        // Gửi request POST đến API
        fetch('http://localhost/food-api/public/api/category', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                showToast("Category", "Thêm thành công!", "bg-success")
                console.log(data);
                loadData()
                // Đóng modal sau khi thêm thành công
                $('#addCategoryModal').modal('hide');
                // Nếu cần làm gì đó sau khi thêm thành công, thêm code vào đây
            })
            .catch(error => {
                // Xử lý lỗi nếu có
                showToast("Category", "Thêm thất bại!", "bg-danger")
                console.error('There has been a problem with your fetch operation:', error);
                // Hiển thị thông báo lỗi cho người dùng nếu cần
            });
    }

    function submitEditCategory() {
        var form = document.getElementById("editCategoryForm");

        if (!form.checkValidity()) {
            showToast("Error", "Please fill in all required fields!", "bg-warning");
            form.classList.add('was-validated');
            return;
        }

        var categoryId = document.getElementById("id").value;
        var categoryName = document.getElementById("editName").value;
        var categoryImage = document.getElementById("editImage").files[0]; // Assuming single file upload

        // Correctly initializing a new FormData object
        var formData = new FormData();
        formData.append('name', categoryName); // Append name
        if (categoryImage) { // Only append the image if one was chosen
            formData.append('image', categoryImage); // Append image
        }

        fetch(`http://localhost/food-api/public/api/category/${categoryId}`, {
            method: 'POST', // Using POST as the method
            headers: {
                'X-HTTP-Method-Override': 'PUT', // Override with PUT for updating
            },
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP status ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                showToast("Category", "Cập nhật thành công!", "bg-success")
                window.location.reload(); // Reload the page to see the changes
            })
            .catch(error => {
                console.error('Error:', error);
                showToast("Category", "Có lỗi xảy ra!", "bg-danger")
            });
    }

    function loadData() {
        fetch('http://localhost/food-api/public/api/category')
            .then(response => response.json())
            .then(data => displayCategories(data))
            .catch(error => console.error('Error:', error));
    }

    document.addEventListener("DOMContentLoaded", function () {
        loadData()
    });

    function displayCategories(categories) {
        const categoryList = document.getElementById('categoryList');
        categoryList.innerHTML = ""
        categories.forEach(category => {
            const row = document.createElement('tr');

            const imageCell = document.createElement('td');
            const image = document.createElement('img');
            image.src = "http://localhost/food-api/storage/app/public/category_images/" + category.image_url;
            image.alt = 'Category Image';
            image.style.width = '100px'; // Set a fixed width for images
            imageCell.appendChild(image);

            const nameCell = document.createElement('td');
            nameCell.textContent = category.name;

            const actionsCell = document.createElement('td');
            actionsCell.innerHTML = `
                <i class="fas fa-edit action-icon" data-bs-toggle="modal" data-bs-target="#editCategoryModal" onclick="editCategory(${category.id})" title="Edit"></i>
                <i class="fas fa-trash action-icon" data-bs-toggle="modal" data-bs-target="#smallModal" onclick="deleteCategory(${category.id})" title="Delete"></i>
            `;

            row.appendChild(imageCell);
            row.appendChild(nameCell);
            row.appendChild(actionsCell);

            categoryList.appendChild(row);
        });
    }


    function editCategory(categoryId) {
        fetch('http://localhost/food-api/public/api/category/' + categoryId, {
            method: 'GET',
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                document.getElementById('id').value = categoryId
                document.getElementById('editName').value = data.name
                document.getElementById('oldImage').src = `http://localhost/food-api/storage/app/public/category_images/${data.image_url}`;
            })
            .catch(error => {
                // Xử lý lỗi nếu có
                showToast("Category", "Có lỗi xảy ra!", "bg-danger")
                console.error('There has been a problem with your fetch operation:', error);
                // Hiển thị thông báo lỗi cho người dùng nếu cần
            });
    }

    function deleteCategory(categoryId) {
        currentDeletingCategoryId = categoryId
    }
    function confirmDelete() {
        if (!currentDeletingCategoryId) return;

        fetch(`http://localhost/food-api/public/api/category/${currentDeletingCategoryId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                showToast("Category", "Xóa thành công!", "bg-success");
                loadData(); // Load lại danh sách category
                $('#smallModal').modal('hide'); // Đóng modal dialog
            })
            .catch(error => {
                showToast("Category", "Xóa thất bại!", "bg-danger");
                console.error('There has been a problem with your fetch operation:', error);
            });
    }
</script>
@endsection