<script>
    import axios from "axios";
    import { actualUser, jwt_token, myUserId, isAuthenticated } from "../store"; //Das JWT wird aus dem Store geladen

    const api_root = window.location.origin;

        
    let cars = [];

    function getCars() {
        var config = {
            method: "get",
            url: api_root + "/api/mycars", //Komplette URL für den Request erstellen, z.B: http://localhost:8080/api/car?pageSize=4&pageNumber=1
            headers: { Authorization: "Bearer " + $jwt_token }, //Das JWT wird im Header mitgeschickt
        };

        axios(config)
            .then(function (response) {
                cars = response.data;
            })
            .catch(function (error) {
                alert("Could not get cars");
                console.log(error);
            });
    }
    getCars();

    function unrentCar(carId) {
        var config = {
            method: "put",
            url: api_root + "/api/service/me/unrentcar?carId=" + carId,
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                getCars();
            })
            .catch(function (error) {
                alert("Could not unrent car");
                console.log(error);
            });
    }

    function deleteMyCarById(carId) {
    var config = {
        method: "delete",
        url: api_root + "/api/me/car/" + carId,
        headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + $jwt_token, //Das JWT wird im Header mitgeschickt
        },
    };

    axios(config)
        .then(function (response) {
            alert("Car deleted");
            getCars();
        })
        .catch(function (error) {
            alert("Could not delete Car");
            console.log(error);
        });
}

</script>

<h1>Account Details</h1>

{#if $isAuthenticated}
    <p><img src={$actualUser.picture} alt="" srcset="" /></p>
    <p><b>Name:</b> {$actualUser.name}</p>
    <p><b>Nickname:</b> {$actualUser.nickname}</p>
    <p><b>Email:</b> {$actualUser.email}</p>
    <!-- Show roles only if user has at least one role -->
    {#if $actualUser.user_roles && $actualUser.user_roles.length > 0}
        <p><b>Roles:</b> {$actualUser.user_roles}</p>
    {/if}
{:else}
    <p>Not logged in</p>
{/if}

<h1>All Cars</h1>

<table class="table">
    <thead>
        <tr>
            <th>Image</th>
            <th scope="col">Brand</th>
            <th scope="col">Model</th>
            <th scope="col">Year</th>
            <th scope="col">Area</th>
            <th scope="col">Price</th>
            <th scope="col">Type</th>
            <th scope="col">Transmission</th>
            <th scope="col">State</th>
            <th scope="col">Rented By</th>
            <th scope="col">Owner</th>
            <th scope="col">Actions</th>
        </tr>
    </thead>
    <tbody>
        {#each cars as car}
            <tr>
                <td><img src={"images/" + car.model + ".jpg"} alt={car.model} width="200" /></td>
                <td>{car.brand}</td>
                <td>{car.model}</td>
                <td>{car.year}</td>
                <td>{car.carArea}</td>
                <td>{car.price}</td>
                <td>{car.carType}</td>
                <td>{car.carTransmission}</td>
                <td>{car.carState}</td>
                <td>{car.userEmail}</td>
                <td>{car.ownerEmail}</td>
                <td>
                    {#if car.userId === $myUserId}
                        <button
                            type="button"
                            class="btn btn-success btn-sm"
                            on:click={() => {
                                unrentCar(car.id);
                            }}>Unrent Car</button
                        >
                    {:else if car.ownerId === $myUserId}
                    <span class="badge bg-secondary" id="myCar"
                    >My Car</span>
                    <button
                            type="button"
                            class="btn btn-danger btn-sm"
                            id="deleteButton"
                            on:click={() => {deleteMyCarById(car.id);}}>Delete</button
                        >
                    {/if}
                </td>
            </tr>
        {/each}
    </tbody>
</table>