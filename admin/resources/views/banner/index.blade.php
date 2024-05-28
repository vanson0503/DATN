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
        <h3 class="my-4">Banner List</h3>
        <button class="btn btn-success btn-add" data-bs-toggle="modal" data-bs-target="#addBannerModal">
            <i class="fas fa-plus"></i> Add Banner
        </button>
        <table class="table table-bordered table-hover">
            <thead class="thead-light">
                <tr>
                    <th>Image</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="bannerList">
                <!-- banners will be loaded here dynamically -->
            </tbody>
        </table>
    </div>


    <div class="modal fade" id="addBannerModal" tabindex="-1" aria-labelledby="addBannerModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addBannerModalLabel">Add category</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <form id="addBannerForm" enctype="multipart/form-data">

                        <label for="image">Banner Image:</label><br>
                        <input type="file" class="form-control" id="image" name="image" accept="image/*"
                            required><br><br>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" onclick="addBanner()">Add Banner</button>
                </div>
            </div>
        </div>
    </div>


    <div class="modal fade" id="smallModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-sm" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel2">Banner</h5>
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
    var currentDeletingBannerId = null;
    function addBanner() {

        var form = document.getElementById("addBannerForm");

        // Check form validity
        if (!form.checkValidity()) {
            // If the form is not valid, display an error message
            showToast("Lỗi", "Vui lòng điền đầy đủ thông tin!", "bg-warning");
            form.classList.add('was-validated'); // Bootstrap class to handle form validation feedback
            return; // Stop the function
        }

        // Lấy dữ liệu từ form
        var formData = new FormData(document.getElementById("addBannerForm"));


        // Gửi request POST đến API
        fetch('http://localhost/food-api/public/api/banner', {
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
                showToast("Banner", "Thêm thành công!", "bg-success")
                console.log(data);
                loadData()
                // Đóng modal sau khi thêm thành công
                $('#addBannerModal').modal('hide');
                // Nếu cần làm gì đó sau khi thêm thành công, thêm code vào đây
            })
            .catch(error => {
                // Xử lý lỗi nếu có
                showToast("Banner", "Thêm thất bại!", "bg-danger")
                console.error('There has been a problem with your fetch operation:', error);
                // Hiển thị thông báo lỗi cho người dùng nếu cần
            });
    }

    function loadData() {
        fetch('http://localhost/food-api/public/api/banner')
            .then(response => response.json())
            .then(data => displayBanners(data))
            .catch(error => console.error('Error:', error));
    }

    document.addEventListener("DOMContentLoaded", function () {
        loadData()
    });

    function displayBanners(banners) {
        const bannerList = document.getElementById('bannerList');
        bannerList.innerHTML = ""
        banners.forEach(banner => {
            const row = document.createElement('tr');

            const imageCell = document.createElement('td');
            const image = document.createElement('img');
            if (banner.img_url.startsWith("https")) {
                image.src =  banner.img_url;
            }
            else{
                image.src = "http://localhost/food-api/storage/app/public/banner_images/" + banner.img_url;
            }
            image.alt = 'Banner Image';
            image.style.width = '100px'; // Set a fixed width for images
            imageCell.appendChild(image);



            const actionsCell = document.createElement('td');
            actionsCell.innerHTML = `
                <i class="fas fa-trash action-icon" data-bs-toggle="modal" data-bs-target="#smallModal" onclick="deleteBanner(${banner.id})" title="Delete"></i>
            `;

            row.appendChild(imageCell);
            row.appendChild(actionsCell);

            bannerList.appendChild(row);
        });
    }

    function deleteBanner(bannerId) {
        currentDeletingBannerId = bannerId
    }
    function confirmDelete() {
        if (!currentDeletingBannerId) return;

        fetch(`http://localhost/food-api/public/api/banner/${currentDeletingBannerId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                showToast("Banner", "Xóa thành công!", "bg-success");
                loadData(); // Load lại danh sách category
                $('#smallModal').modal('hide'); // Đóng modal dialog
            })
            .catch(error => {
                showToast("Banner", "Xóa thất bại!", "bg-danger");
                console.error('There has been a problem with your fetch operation:', error);
            });
    }
</script>
@endsection