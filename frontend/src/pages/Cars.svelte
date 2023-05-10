<script>
    import axios from "axios";
    import { actualUser, jwt_token, myUserId } from "../store"; //Das JWT wird aus dem Store geladen
    import { querystring } from "svelte-spa-router"; //Wird benötigt, um Query-Parameter aus der aktuellen URL auszulesen, z.B.: http://localhost:8080/#/cars?pageNumber=2

    const api_root = window.location.origin;
    /*
    Hinweis: window.location.origin ist die Serveradresse der aktuellen Seiten. Beispiel: Wenn
    http://localhost:8080/#/cars angezeigt wird, ist window.location.origin gleich
    http://localhost:8080
    Dies hat den Vorteil, dass wir die URL später nicht anpassen müssen, wenn wir die Anwendung
    deployen.
    */

    let currentPage;
    let nrOfPages = 0;
    let defaultPageSize = 4;
    /*In diesen Variablen merken wir uns, welche
    Page aktuell angezeigt wird und wie viele
    Pages es insgesamt gibt.*/

    let priceMax;
    let carType;
    let carTransmission; //In den Input-Elementen eingetragene Werte

    let cars = [];
    /*let car = {
        brand: null,
        model: null,
        price: null,
        carType: null,
        carTransmission: null,
        description: null,
    };*/

    $: {
        let searchParams = new URLSearchParams($querystring);
        if (searchParams.has("page")) {
            currentPage = searchParams.get("page");
        } else {
            currentPage = "1";
        }
        getCars();
    }
    /* Dieser Code wird immer dann ausgeführt, wenn sich der Wert einer Variablen im Code-Block $: {... } ändert,
    siehe auch https://svelte.dev/tutorial/reactive-statements
    Wir lesen hier den Query-Parameter "page" aus der URL und holen uns anschliessend alle Cars. */

    function getCars() {
        let query =
            "?pageSize=" + defaultPageSize + " &pageNumber=" + currentPage; //Hier werden die Query-Parameter für den Request ans Backend erstellt

        if (priceMax) {
            query += "&price=" + priceMax;
        }
        if (carType && carType !== "ALL") {
            query += "&type=" + carType;
        }
        if (carTransmission && carTransmission !== "ALL") {
            query += "&transmission=" + carTransmission;
        }
        /* Query-Parameter für den Request ans Backend ergänzen. Beispiel für eine komplette URL:
        http://localhost:8080/api/car?pageSize=4&page=2&price=139&carType=TEST */

        var config = {
            method: "get",
            url: api_root + "/api/car" + query, //Komplette URL für den Request erstellen, z.B: http://localhost:8080/api/car?pageSize=4&pageNumber=1
            headers: { Authorization: "Bearer " + $jwt_token }, //Das JWT wird im Header mitgeschickt
        };

        axios(config)
            .then(function (response) {
                cars = response.data.content;
                nrOfPages = response.data.totalPages; //Nach jedem Request wird die Anzahl Pages aktualisiert. Das Backend schickt diese in der Property totalPages in der Response.
            })
            .catch(function (error) {
                alert("Could not get cars");
                console.log(error);
            });
    }
    //getCars();
    /* getCars() wird neu im Reactive Statement weiter
    oben aufgerufen und kann hier gelöscht oder
    auskommentiert werden. */

    /*
    function createCar() {
        var config = {
            method: "post",
            url: api_root + "/api/car",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token, //Das JWT wird im Header mitgeschickt
            },
            data: car,
        };

        axios(config)
            .then(function (response) {
                alert("Car created");
                getCars();
            })
            .catch(function (error) {
                alert("Could not create Car");
                console.log(error);
            });
    }
    */

    function rentCar(carId) {
        var config = {
            method: "put",
            url: api_root + "/api/service/me/rentcar?carId=" + carId,
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                getCars();
            })
            .catch(function (error) {
                alert("Could not rent car");
                console.log(error);
            });
    }

    /*
    function getMyUserId() {
        var config = {
            method: "get",
            url: api_root + "/api/me/user",
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                $myUserId = response.data.id;
            })
            .catch(function (error) {
                alert("Could not get user ID");
                console.log(error);
            });
    }
    getMyUserId();
    */

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
            data: cars,
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

<h1>All Cars</h1>

<div class="row my-3">
    <div class="col-auto">
        <label for="" class="col-form-label">Price: </label>
    </div>
    <div class="col-3">
        <input
            class="form-control"
            type="number"
            placeholder="price"
            id="minPriceFilter"
            bind:value={priceMax}
        />
        <!--Die Eingaben sind via value-binding mit den Variablen priceMax und carType (siehe nächste Seite) verknüpft.
        Für den Apply-Button braucht es keine neue Funktion, wir müssen nur die URL mit den neuen Query-Parametern aktualisieren.
        Dadurch werden via Reactive Statement die Cars neu geladen. //-->
    </div>
    <div class="col-auto">
        <label for="" class="col-form-label">Car Type: </label>
    </div>
    <div class="col-3">
        <select
            bind:value={carType}
            class="form-select"
            id="carTypeFilter"
            type="text"
        >
            <option value="ALL" />
            <option value="ELECTRIC">ELECTRIC</option>
            <option value="HYBRID">HYBRID</option>
            <option value="DIESEL">DIESEL</option>
            <option value="GAS">GAS</option>
        </select>
    </div>
    <div class="col-auto">
        <label for="" class="col-form-label">Transmission: </label>
    </div>
    <div class="col-3">
        <select
            bind:value={carTransmission}
            class="form-select"
            id="carTransmissionFilter"
            type="text"
        >
            <option value="ALL" />
            <option value="MANUAL">MANUAL</option>
            <option value="AUTOMATIC">AUTOMATIC</option>
            <option value="SINGLE">SINGLE</option>
        </select>
    </div>
    <div class="col-3">
        <a
            class="btn btn-primary"
            id="applyButton"
            href={"#/cars?page=1&carType=" +
                carType +
                "&price=" +
                priceMax +
                "&carTransmission=" +
                carTransmission}
            role="button">Apply</a
        >
    </div>
</div>

<table class="table">
    <thead>
        <tr>
            <th />
            <th scope="col">Brand</th>
            <th scope="col">Model</th>
            <th scope="col">Price</th>
            <th scope="col">Type</th>
            <th scope="col">Transmission</th>
            <th scope="col">State</th>
            <th scope="col">User</th>
            <th scope="col">Actions</th>
        </tr>
    </thead>
    <tbody>
        {#each cars as car}
            <tr>
                <td>
                    <a
                        href={"#/car/" + car.id}
                        class="btn btn-primary"><i class="fa fa-bars" /></a
                    >
                </td>
                <td>{car.brand}</td>
                <td>{car.model}</td>
                <td>{car.price}</td>
                <td>{car.carType}</td>
                <td>{car.carTransmission}</td>
                <td>{car.carState}</td>
                <td>{car.userId}</td>
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
                    {:else if car.userId === null && car.ownerId !== $myUserId}
                        <button
                            type="button"
                            class="btn btn-primary btn-sm"
                            id="rentButton"
                            on:click={() => {rentCar(car.id);}}>Rent Car</button
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
                            on:click={() => {deleteCar(car.id);}}>Delete</button
                        >
                    {/if}
                </td>
            </tr>
        {/each}
    </tbody>
</table>
<nav>
    <ul class="pagination">
        {#each Array(nrOfPages) as _, i}
            <!-- In each-Blöcken kann man nur über Arrays iterieren. Wir wissen aber nur, wie viele Page-
        Links hinzugefügt werden sollen (nrOfPages). Der Trick ist, erst ein Array mit nrOfPages
        Elementen zu erstellen. Diese Elemente sind leer und interessieren uns auch nicht, darum
        bezeichnen wir sie mit _. Aber wir können den Index i im each-Block verwenden, um
        beispielsweise die Pagination-Elemente zu beschriften. //-->
            <li class="page-item">
                <a
                    class="page-link"
                    class:active={currentPage == i + 1}
                    href={"#/cars?page=" + (i + 1)}
                    >{i + 1}
                </a>
                <!-- Pagination-Element wird blau (active) angezeigt, wenn die aktuelle Page mit dem index (+1) übereinstimmt.
                    
                Bei Klick auf das Pagination-Element soll auf die entsprechende Page gewechselt werden, z.B. http://localhost:8080/#/cars?page=2//-->
            </li>
        {/each}
    </ul>
</nav>
