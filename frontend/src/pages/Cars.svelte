<script>
    import axios from "axios";
    import { user, jwt_token, myMieterId } from "../store"; //Das JWT wird aus dem Store geladen
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

    let priceMin;
    let carType;
    let carTransmission; //In den Input-Elementen eingetragene Werte

    let cars = [];
    let car = {
        brand: null,
        model: null,
        price: null,
        carType: null,
        carTransmission: null,
        description: null,
    };

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

        if (priceMin) {
            query += "&price=" + priceMin;
        }
        if (carType && carType !== "ALL") {
            query += "&type=" + carType;
        }
        if (carTransmission && carTransmission !== "ALL") {
            query += "&type=" + carTransmission;
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
    function getMyMieterId() {
        var config = {
            method: "get",
            url: api_root + "/api/me/mieter",
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                $myMieterId = response.data.id;
            })
            .catch(function (error) {
                alert("Could not get mieter ID");
                console.log(error);
            });
    }
    getMyMieterId();
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
</script>

{#if $user.user_roles && $user.user_roles.includes("vermieter")}
    <h1 class="mt-3">Create Car</h1>
    <form class="mb-5">
        <div class="row mb-3">
            <div class="col">
                <label class="form-label" for="brand">Brand</label>
                <input bind:value={car.brand} class="form-control" id="brand" type="text"/>
            </div>
            <div class="col">
                <label class="form-label" for="model">Model</label>
                <input bind:value={car.model} class="form-control" id="model" type="text"/>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col">
                <label class="form-label" for="description">Description</label>
                <input bind:value={car.description} class="form-control" id="description" type="text"/>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col">
                <label class="form-label" for="cartype">Cartype</label>
                <select bind:value={car.carType} class="form-select" id="cartype" type="text">
                    <option value="ELECTRIC">ELECTRIC</option>
                    <option value="HYBRID">HYBRID</option>
                    <option value="DIESEL">DIESEL</option>
                    <option value="GAS">GAS</option>
                </select>
            </div>
            <div class="col">
                <label class="form-label" for="transmission">Transmission</label>
                <select bind:value={car.carTransmission} class="form-select" id="transmission" type="text">
                    <option value="MANUAL">MANUAL</option>
                    <option value="AUTOMATIC">AUTOMATIC</option>
                    <option value="SINGLE">SINGLE</option>
                </select>
            </div>
            <div class="col">
                <label class="form-label" for="price">Price</label>
                <input bind:value={car.price} class="form-control" id="price" type="number"/>
            </div>
        </div>
        <button type="button" class="btn btn-primary" id="submitbutton" on:click={createCar}>Submit</button>
    </form>
{/if}

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
            bind:value={priceMin}
        />
        <!--Die Eingaben sind via value-binding mit den Variablen priceMin und carType (siehe nächste Seite) verknüpft.
        Für den Apply-Button braucht es keine neue Funktion, wir müssen nur die URL mit den neuen Query-Parametern aktualisieren.
        Dadurch werden via Reactive Statement die Cars neu geladen. //-->
    </div>
    <div class="col-auto">
        <label for="" class="col-form-label">Car Type: </label>
    </div>
    <div class="col-3">
        <select bind:value={carType} class="form-select" id="carTypeFilter" type="text">
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
        <select bind:value={carTransmission} class="form-select" id="carTransmissionFilter" type="text">
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
            href={"#/cars?page=1&carType=" + carType + "&price=" + priceMin + "&carTransmission=" + carTransmission}
            role="button">Apply</a
        >
    </div>
</div>

<table class="table">
    <thead>
        <tr>
            <th scope="col">Brand</th>
            <th scope="col">Model</th>
            <th scope="col">Price</th>
            <th scope="col">Type</th>
            <th scope="col">Transmission</th>
            <th scope="col">State</th>
            <th scope="col">MieterId</th>
            <th scope="col">Actions</th>
        </tr>
    </thead>
    <tbody>
        {#each cars as car}
            <tr>
                <td>{car.brand}</td>
                <td>{car.model}</td>
                <td>{car.price}</td>
                <td>{car.cartype}</td>
                <td>{car.carTransmission}</td>
                <td>{car.carState}</td>
                <td>{car.mieterId}</td>
                <td>
                    {#if car.carState === "UNAVAILABLE" && car.mieterId !== $myMieterId}
                        <span class="badge bg-secondary" id="rented">Unavailable</span>
                    {:else if car.mieterId === null}
                        <button type="button" class="btn btn-primary btn-sm" id="rentButton"
                            on:click={() => {rentCar(car.id);}}>Rent Car</button>
                    {/if}

                    {#if car.mieterId === $myMieterId && car.carState !== "UNAVAILABLE"}
                        <button type="button" class="btn btn-success btn-sm"
                            on:click={() => {unrentCar(car.id);}}>Unrent Car</button>
                    {/if}
                </td>
                <!-- Wenn der Car den Zustand «ASSIGNED» hat, wird ein Badge mit dem Text «Assigned» angezeigt.
                Wenn dem Car noch kein Freelancer zugewiesen ist (mieterId ist null), wird der Button «Assign To Me» angezeigt. //-->
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
