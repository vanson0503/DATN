package com.example.foodapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.foodapp.R
import com.example.foodapp.data.api.RetrofitClient
import com.example.foodapp.data.repository.LocationRepository
import com.example.foodapp.model.location.LocationItem
import com.example.foodapp.ui.theme.SoftCoral
import com.example.foodapp.utils.Province
import com.example.foodapp.utils.isValidPhoneNumber
import com.example.foodapp.utils.loadProvinces
import com.example.foodapp.viewmodel.LocationViewModel


@Composable
fun LocationDropdown(
    locations: List<LocationItem>,
    onLocationChange: (Int) -> Unit,
    onAddLocation: () -> Unit  // Thêm callback này để xử lý việc thêm địa điểm
) {
    var expanded by remember { mutableStateOf(false) }
    val defaultLocation = locations.firstOrNull { it.is_default == 1 }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (locations.isEmpty()) {
            // Hiển thị nút Add khi danh sách trống
            Button(
                onClick = { onAddLocation() },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Location",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm địa điểm")
            }
        } else {
            // Nếu có địa điểm thì hiển thị dropdown như bình thường
            val selectedLocation = remember { mutableStateOf(defaultLocation ?: locations.first()) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { expanded = true }
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier.size(24.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "${selectedLocation.value.name} - ${selectedLocation.value.phone_number}",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface
                    )
                    Text(
                        text = selectedLocation.value.address,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface
                    )
                    if (selectedLocation.value.is_default == 1) {
                        Text(
                            text = "Mặc định",
                            color = Color.Red,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    locations.forEachIndexed { index, location ->
                        DropdownMenuItem(
                            onClick = {
                                onLocationChange(location.id)
                                selectedLocation.value = location
                                expanded = false
                            }
                        ) {
                            Column {
                                Text(
                                    text = location.name,
                                    style = MaterialTheme.typography.subtitle2
                                )
                                Text(
                                    text = location.phone_number,
                                    style = MaterialTheme.typography.body2
                                )
                                Text(
                                    text = location.address,
                                    style = MaterialTheme.typography.body1
                                )
                                if (location.is_default == 1) {
                                    Text(
                                        text = "Mặc định",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.caption
                                    )
                                }
                                if (index < locations.size - 1) {
                                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddLocationScreen(
    onBackClicked: () -> Unit,
    onAddSuccess:()->Unit
) {
    val context = LocalContext.current
    val name = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val isDefault = remember { mutableStateOf(false) }
    val (loading, setLoading) = remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val locationRepository = remember { LocationRepository(RetrofitClient.locationApiService) }
    val locationViewModel: LocationViewModel = remember { LocationViewModel(locationRepository) }
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)
    val location = remember { mutableStateOf("") }


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Thêm địa chỉ") },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        }
    ) {paddingValues->
        Column(
            Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Họ tên") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phoneNumber.value,
                    onValueChange = { phoneNumber.value = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth()
                )

                SelectLocationScreen{
                    location.value = it
                }
                OutlinedTextField(
                    value = address.value,
                    onValueChange = { address.value = it },
                    label = { Text("Địa chỉ chi tiết") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDefault.value,
                        onCheckedChange = { isDefault.value = it }
                    )
                    Text("Đặt làm mặc định")
                }
                Button(
                    onClick = {
                        if(isValidPhoneNumber(phoneNumber.value)){
                            setLoading(true)
                            locationViewModel.addLocation(
                                customerId = customerId,
                                name = name.value,
                                phoneNumber = phoneNumber.value,
                                address = address.value+", "+location.value,
                                isDefault = isDefault.value,
                                onResult = {result->
                                    if(result){
                                        setLoading(false)
                                        Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show()
                                        onAddSuccess()
                                    }
                                    else{
                                        setLoading(false)
                                        Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                        else{
                            Toast.makeText(context, "Vui lòng nhập đúng định dạng số điện thoại!", Toast.LENGTH_SHORT).show()
                        }

                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = name.value.isNotBlank() && phoneNumber.value.isNotBlank() && address.value.isNotBlank() && !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    } else {
                        Text("Thêm địa chỉ")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditLocationScreen(
    id:Int,
    onBackClicked: () -> Unit,
    onEditSuccess:()->Unit,
    onDeleteSuccess:()->Unit
) {
    val context = LocalContext.current
    val (loading, setLoading) = remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val locationRepository = remember { LocationRepository(RetrofitClient.locationApiService) }
    val locationViewModel: LocationViewModel = remember { LocationViewModel(locationRepository) }
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)

    LaunchedEffect(id) {
        locationViewModel.getLocationById(id)
    }

    val location by locationViewModel.getLocationById.observeAsState()

    if (location == null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.material3.CircularProgressIndicator(color = Color.Black)
            Text("Tải dữ liệu...")
        }
        return
    }

    val name = remember { mutableStateOf(location!!.name) }
    val phoneNumber = remember { mutableStateOf(location!!.phone_number) }
    val address = remember { mutableStateOf(location!!.address) }
    val isDefault = remember { mutableStateOf(location!!.is_default==1) }
    var showDeleteDialog by remember { mutableStateOf(false) }



    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Sửa địa chỉ") },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        }
    ) {paddingValues->
        Column(
            Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phoneNumber.value,
                    onValueChange = { phoneNumber.value = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address.value,
                    onValueChange = { address.value = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDefault.value,
                        onCheckedChange = { isDefault.value = it }
                    )
                    Text("Đặt làm mặc định")
                }
                Button(
                    onClick = {
                              locationViewModel.deleteLocation(
                                  id,
                                  onResult = {result->
                                      if(result){
                                          Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                                          onDeleteSuccess()
                                      }else{
                                          Toast.makeText(context, "Có lỗi khi xóa", Toast.LENGTH_SHORT).show()
                                      }
                                  }
                              )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RectangleShape,
                ) {
                    Text(
                        text = "Xóa địa chỉ",
                        color = Color.Red,
                        modifier = Modifier.clickable {
                            showDeleteDialog = true
                        }
                    )
                }
                Button(
                    onClick = {
                        setLoading(true)
                        locationViewModel.editLocation(
                            id,
                            customerId = customerId,
                            name = name.value,
                            phoneNumber = phoneNumber.value,
                            address = address.value,
                            isDefault = isDefault.value,
                            onResult = {result->
                                if(result){
                                    setLoading(false)
                                    Toast.makeText(context, "Sửa thành công", Toast.LENGTH_SHORT).show()
                                    onEditSuccess()
                                }
                                else{
                                    setLoading(false)
                                    Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = name.value.isNotBlank() && phoneNumber.value.isNotBlank() && address.value.isNotBlank() && !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    } else {
                        Text("Edit Location")
                    }
                }
                
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        // Ẩn hộp thoại khi người dùng nhấn nút hủy hoặc nút ngoài cùng
                        showDeleteDialog = false
                    },
                    title = { Text("Xác nhận") },
                    text = { Text("Bạn có chắc chắn muốn xóa địa chỉ này?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Thực hiện hành động xóa và đóng hộp thoại
                                locationViewModel.deleteLocation(
                                    id,
                                    onResult = {result->
                                        if(result){
                                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                                            onDeleteSuccess()
                                        }else{
                                            Toast.makeText(context, "Có lỗi khi xóa", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                                showDeleteDialog = false
                            }
                        ) {
                            Text("Xác nhận")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                // Đóng hộp thoại khi người dùng nhấn nút hủy
                                showDeleteDialog = false
                            }
                        ) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LocationListScreen(
    onBackClicked: () -> Unit,
    onEditLocation:(Int)->Unit,
    onAddLocation:()->Unit
) {
    val context = LocalContext.current
    val locationRepository = remember { LocationRepository(RetrofitClient.locationApiService) }
    val locationViewModel: LocationViewModel = remember { LocationViewModel(locationRepository) }
    val sharedPreferences = context.getSharedPreferences("customer_data", Context.MODE_PRIVATE)
    val customerId = sharedPreferences.getInt("customer_id",-1)


    LaunchedEffect(customerId) {
        locationViewModel.getLocationsByCustomerId(customerId)
    }

    val locations by locationViewModel.getLocationsByCustomerId.observeAsState()

    if (locations==null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.material3.CircularProgressIndicator(color = Color.Black)
            Text("Loading data...")
        }
        return
    }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Địa chỉ") },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        }
    ) {paddingValues->
        Column(
            Modifier.padding(paddingValues)
        ) {
            Column(
                Modifier.padding(horizontal = 16.dp)
            ) {
                if(locations!!.isEmpty()){
                    EmptyLocation(
                        onAddLocation = {onAddLocation()}
                    )
                }
                else{
                    LazyColumn {
                        item {
                            repeat(locations!!.size){index->
                                Row(
                                    Modifier.padding(vertical = 10.dp)
                                ) {
                                    LocationCard(
                                        locations!![index]
                                        ,
                                        onEditLocation = {id->
                                            onEditLocation(id)
                                        }
                                    )
                                }
                            }
                        }
                        item {

                            Button(
                                onClick = {
                                    onAddLocation()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text("Thêm địa chỉ mới")
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun EmptyLocation(
    onAddLocation: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Chưa có địa chỉ nào", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddLocation) {
            Text("Thêm địa chỉ")
        }
    }
}

@Composable
fun LocationCard(
    location: LocationItem,
    onEditLocation: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .background(color = Color(0xFFF0F5FA), shape = RoundedCornerShape(size = 16.dp))
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(location.is_default == 1){
                Icon(
                    painter = painterResource(id = R.drawable.ping),
                    contentDescription = null,
                    tint = SoftCoral,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = location.name, style = MaterialTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(4.dp)) // Khoảng cách giữa các Text
                Text(text = location.phone_number, style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(4.dp)) // Khoảng cách giữa các Text
                Text(text = location.address, style = MaterialTheme.typography.body2)
                if(location.is_default == 1){
                    Spacer(modifier = Modifier.height(4.dp)) // Khoảng cách giữa các Text
                    Text(text = "Mặc định", style = MaterialTheme.typography.body2.copy(color = Color.Red))
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = { onEditLocation(location.id) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}

@Composable
fun SelectLocationScreen(
    onSelectLocation: (String) -> Unit
) {
    val context = LocalContext.current
    val provinces: List<Province> = loadProvinces(context)
    var selectedProvinceIndex by remember { mutableStateOf(0) }
    var selectedDistrictIndex by remember { mutableStateOf(0) }
    var selectedWardIndex by remember { mutableStateOf(0) }

    // Variables to store the selected location details
    var selectedProvinceName by remember { mutableStateOf("") }
    var selectedDistrictName by remember { mutableStateOf("") }
    var selectedWardName by remember { mutableStateOf("") }

    // Use LaunchedEffect to automatically trigger the selection callback when indices change
    LaunchedEffect(selectedProvinceIndex, selectedDistrictIndex, selectedWardIndex) {
        // Get the selected location details
        selectedProvinceName =
            provinces[selectedProvinceIndex].province_name
        selectedDistrictName =
            provinces[selectedProvinceIndex].districts[selectedDistrictIndex].district_name
        selectedWardName =
            provinces[selectedProvinceIndex].districts[selectedDistrictIndex].wards[selectedWardIndex].ward_name
        val address = "$selectedWardName, $selectedDistrictName, $selectedProvinceName"
        // Pass the selected location details to the callback function
        onSelectLocation(
            address
        )
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Địa chỉ:")
        Row {
            Text("Tỉnh - Thành phố: ")
            DropdownMenu(
                items = provinces.map { it.province_name },
                selectedIndex = selectedProvinceIndex,
                onItemSelected = { index ->
                    selectedProvinceIndex = index
                    // Reset district and ward indices
                    selectedDistrictIndex = 0
                    selectedWardIndex = 0
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Select District
        Row {
            Text("Huyện: ")
            DropdownMenu(
                items = provinces[selectedProvinceIndex].districts.map { it.district_name },
                selectedIndex = selectedDistrictIndex,
                onItemSelected = { index ->
                    selectedDistrictIndex = index
                    // Reset ward index
                    selectedWardIndex = 0
                },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Xã: ")
            val wards = provinces[selectedProvinceIndex].districts[selectedDistrictIndex].wards
            if (wards.isNotEmpty()) {
                DropdownMenu(
                    items = wards.map { it.ward_name },
                    selectedIndex = selectedWardIndex,
                    onItemSelected = { index ->
                        selectedWardIndex = index
                    },
                )
            } else {
                selectedWardIndex = -1 // Reset the selected index if there are no wards
                Text("Không có xã")
            }
        }

    }
}

@Composable
fun DropdownMenu(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            items[selectedIndex],
            modifier = Modifier.clickable { expanded = true }
        )

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                LazyColumn {
                    itemsIndexed(items) { index, item ->
                        Text(
                            item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onItemSelected(index)
                                    expanded = false
                                }
                        )
                    }
                }
            }
        }
    }
}


