<script>
    import axios from "axios";
    import { jwt_token } from "../store"; //Das JWT wird aus dem Store geladen
    import { querystring } from "svelte-spa-router";

    const api_root = window.location.origin;
    /*
    Hinweis: window.location.origin ist die Serveradresse der aktuellen Seiten. Beispiel: Wenn
    http://localhost:8080/#/mieter angezeigt wird, ist window.location.origin gleich
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

    let mieters = [];
    let mieter = {
        name: null,
        email: null,
    };

    $: {
        let searchParams = new URLSearchParams($querystring);
        if (searchParams.has("page")) {
            currentPage = searchParams.get("page");
        } else {
            currentPage = "1";
        }
        getMieters();
    }

    function getMieters() {
        let query =
            "?pageSize=" + defaultPageSize + "&pageNumber=" + currentPage;

        var config = {
            method: "get",
            url: api_root + "/api/mieter" + query,
            headers: { Authorization: "Bearer " + $jwt_token }, //Das JWT wird im Header mitgeschickt
        };

        axios(config)
            .then(function (response) {
                mieters = response.data.content;
                nrOfPages = response.data.totalPages;
            })
            .catch(function (error) {
                alert("Could not get mieters");
                console.log(error);
            });
    }

    /*
    function validateEmailAndcreateMieter() {
        var config = {
            method: "get",
            url: "https://disify.com/api/email/" + mieter.email, //API-URL mit E-Mail
        };
        axios(config)
            .then(function (response) {
                console.log("Validated email " + mieter.email);
                console.log(response.data);
                if ( //Validierung ob E-Mail gültig ist.
                    response.data.format &&
                    !response.data.disposable &&
                    response.data.dns
                ) {
                    createMieter();
                } else {
                    alert("Email " + mieter.email + " is not valid.");
                }
            })
            .catch(function (error) {
                alert("Could not validate email");
                console.log(error);
            });
    }
    */

    function createMieter() {
        var config = {
            method: "post",
            url: api_root + "/api/mieter",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token, //Das JWT wird im Header mitgeschickt
            },
            data: mieter,
        };

        axios(config)
            .then(function (response) {
                alert("Mieter created");
                getMieters();
            })
            .catch(function (error) {
                alert("Could not create Mieter");
                console.log(error);
            });
    }
</script>

<h1 class="mt-3">Create Mieter</h1>
<form class="mb-5">
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="name">Name</label>
            <input
                bind:value={mieter.name}
                class="form-control"
                id="name"
                type="text"
            />
        </div>
    </div>
    <div class="row mb-3">
        <div class="col">
            <label class="form-label" for="email">Email</label>
            <input
                bind:value={mieter.email}
                class="form-control"
                id="email"
                type="text"
            />
        </div>
    </div>
    <button type="button" class="btn btn-primary" on:click={createMieter}
        >Submit</button
    >
</form>

<h1>All Mieter</h1>
<table class="table">
    <thead>
        <tr>
            <th scope="col">Name</th>
            <th scope="col">Email</th>
        </tr>
    </thead>
    <tbody>
        {#each mieters as mieter}
            <tr>
                <td>{mieter.name}</td>
                <td>{mieter.email}</td>
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
                    href={"#/mieters?page=" + (i + 1)}
                    >{i + 1}
                </a>
                <!-- Pagination-Element wird blau (active) angezeigt, wenn die aktuelle Page mit dem index (+1) übereinstimmt.
                    
                Bei Klick auf das Pagination-Element soll auf die entsprechende Page gewechselt werden, z.B. http://localhost:8080/#/cars?page=2//-->
            </li>
        {/each}
    </ul>
</nav>
