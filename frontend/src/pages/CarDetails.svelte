<script>
    import axios from "axios";
    import { actualUser, jwt_token, myUserId } from "../store"; //Das JWT wird aus dem Store geladen

    const api_root = window.location.origin;

    let carDetails = [];

    function getCarDetails(carId) {
        var config = {
            method: "get",
            url: api_root + "/api/car/" + carId, //Komplette URL für den Request erstellen, z.B: http://localhost:8080/api/car?pageSize=4&pageNumber=1
            headers: { Authorization: "Bearer " + $jwt_token }, //Das JWT wird im Header mitgeschickt
        };

        axios(config)
            .then(function (response) {
                carDetails = response.data;
            })
            .catch(function (error) {
                alert("Could not get CarDetails");
                console.log(error);
            });
    }
    const carId = window.location.hash.split("/")[2]; // Füge diese Zeile hinzu, um die ID aus der URL zu extrahieren
    getCarDetails(carId); // Füge hier carId als Parameter hinzu

    function rentCar(carId) {
        var config = {
            method: "put",
            url: api_root + "/api/service/me/rentcar?carId=" + carId,
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                getCarDetails();
            })
            .catch(function (error) {
                alert("Could not rent car");
                console.log(error);
            });
    }

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

    function deleteCar(carId) {
        var config = {
            method: "delete",
            url: api_root + "/api/car/" + carId,
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token, //Das JWT wird im Header mitgeschickt
            },
            data: carDetails,
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

<h1>Car Details</h1>

<img src={"images/" + carDetails.model + ".jpg"} alt={carDetails.model} width="350" />

<div class="card" style="width: 18rem;">
    <div class="card-body">
        <h5 class="card-title">{carDetails.brand} {carDetails.model}</h5>
        <h6 class="card-subtitle mb-2 text-muted">Price: {carDetails.price}</h6>
        <p class="card-text">
            Year: {carDetails.year}
            Area: {carDetails.area}
            Type: {carDetails.carType}<br />
            Transmission: {carDetails.carTransmission}<br />
            State: {carDetails.carState}<br />
            Owner: {carDetails.ownerName}<br />
            Renter: {carDetails.userEmail}
        </p>
        <p class="card-text">{carDetails.description}</p>
        {#if carDetails.userId === $myUserId}
                        <button
                            type="button"
                            class="btn btn-success btn-sm"
                            on:click={() => {
                                unrentCar(carDetails.id);
                            }}>Unrent Car</button
                        >
                    {:else if carDetails.ownerId === $myUserId}
                    <span class="badge bg-secondary" id="myCar"
                    >My Car</span>
                    <button
                            type="button"
                            class="btn btn-danger btn-sm"
                            id="deleteButton"
                            on:click={() => {deleteMyCarById(carDetails.id);}}>Delete</button
                        >
                    {:else if carDetails.userId === null && carDetails.ownerId !== $myUserId}
                        <button
                            type="button"
                            class="btn btn-primary btn-sm"
                            id="rentButton"
                            on:click={() => {rentCar(carDetails.id);}}>Rent Car</button
                        >
                    {:else}
                        <span class="badge bg-secondary" id="rented"
                            >Unavailable</span
                        >
                    {/if}
                    {#if $actualUser.user_roles && $actualUser.user_roles.includes("admin")}
                        <button
                            type="button"
                            class="btn btn-danger btn-sm"
                            id="deleteButton"
                            on:click={() => {deleteCar(carDetails.id);}}>Delete</button
                        >
                    {/if}          
    </div>
</div>
