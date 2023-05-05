<script>
    import axios from "axios";
    import { user, jwt_token, myFreelancerId } from "../store"; //Das JWT wird aus dem Store geladen
    import { querystring } from "svelte-spa-router"; //Wird benötigt, um Query-Parameter aus der aktuellen URL auszulesen, z.B.: http://localhost:8080/#/jobs?pageNumber=2

    const api_root = window.location.origin;
    /*
    Hinweis: window.location.origin ist die Serveradresse der aktuellen Seiten. Beispiel: Wenn
    http://localhost:8080/#/jobs angezeigt wird, ist window.location.origin gleich
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

    let earningsMin;
    let jobType; //In den Input-Elementen eingetragene Werte

    let jobs = [];
    let job = {
        description: null,
        earnings: null,
        jobType: null,
    };

    $: {
        let searchParams = new URLSearchParams($querystring);
        if (searchParams.has("page")) {
            currentPage = searchParams.get("page");
        } else {
            currentPage = "1";
        }
        getJobs();
    }
    /* Dieser Code wird immer dann ausgeführt, wenn sich der Wert einer Variablen im Code-Block $: {... } ändert,
    siehe auch https://svelte.dev/tutorial/reactive-statements
    Wir lesen hier den Query-Parameter "page" aus der URL und holen uns anschliessend alle Jobs. */

    function getJobs() {
        let query =
            "?pageSize=" + defaultPageSize + " &pageNumber=" + currentPage; //Hier werden die Query-Parameter für den Request ans Backend erstellt

        if (earningsMin) {
            query += "&min=" + earningsMin;
        }
        if (jobType && jobType !== "ALL") {
            query += "&type=" + jobType;
        }
        /* Query-Parameter für den Request ans Backend ergänzen. Beispiel für eine komplette URL:
        http://localhost:8080/api/job?pageSize=4&page=2&min=139&jobType=TEST */

        var config = {
            method: "get",
            url: api_root + "/api/job" + query, //Komplette URL für den Request erstellen, z.B: http://localhost:8080/api/job?pageSize=4&pageNumber=1
            headers: { Authorization: "Bearer " + $jwt_token }, //Das JWT wird im Header mitgeschickt
        };

        axios(config)
            .then(function (response) {
                jobs = response.data.content;

                nrOfPages = response.data.totalPages; //Nach jedem Request wird die Anzahl Pages aktualisiert. Das Backend schickt diese in der Property totalPages in der Response.
            })
            .catch(function (error) {
                alert("Could not get jobs");
                console.log(error);
            });
    }
    //getJobs();
    /* getJobs() wird neu im Reactive Statement weiter
    oben aufgerufen und kann hier gelöscht oder
    auskommentiert werden. */

    function createJob() {
        var config = {
            method: "post",
            url: api_root + "/api/job",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + $jwt_token, //Das JWT wird im Header mitgeschickt
            },
            data: job,
        };

        axios(config)
            .then(function (response) {
                alert("Job created");
                getJobs();
            })
            .catch(function (error) {
                alert("Could not create Job");
                console.log(error);
            });
    }

    function assignToMe(jobId) {
        var config = {
            method: "put",
            url: api_root + "/api/service/me/assignjob?jobId=" + jobId,
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                getJobs();
            })
            .catch(function (error) {
                alert("Could not assign job to me");
                console.log(error);
            });
    }

    /*
    function getMyFreelancerId() {
        var config = {
            method: "get",
            url: api_root + "/api/me/freelancer",
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                $myFreelancerId = response.data.id;
            })
            .catch(function (error) {
                alert("Could not get freelancer ID");
                console.log(error);
            });
    }
    getMyFreelancerId();
    */

    function completeJob(jobId) {
        var config = {
            method: "put",
            url: api_root + "/api/service/me/completejob?jobId=" + jobId,
            headers: { Authorization: "Bearer " + $jwt_token },
        };
        axios(config)
            .then(function (response) {
                getJobs();
            })
            .catch(function (error) {
                alert("Could not complete job");
                console.log(error);
            });
    }
</script>

{#if $user.user_roles && $user.user_roles.includes("admin")}
    <h1 class="mt-3">Create Job</h1>
    <form class="mb-5">
        <div class="row mb-3">
            <div class="col">
                <label class="form-label" for="description">Description</label>
                <input
                    bind:value={job.description}
                    class="form-control"
                    id="description"
                    type="text"
                />
            </div>
        </div>
        <div class="row mb-3">
            <div class="col">
                <label class="form-label" for="type">Type</label>
                <select
                    bind:value={job.jobType}
                    class="form-select"
                    id="type"
                    type="text"
                >
                    <option value="OTHER">OTHER</option>
                    <option value="TEST">TEST</option>
                    <option value="IMPLEMENT">IMPLEMENT</option>
                    <option value="REVIEW">REVIEW</option>
                </select>
            </div>
            <div class="col">
                <label class="form-label" for="earnings">Earnings</label>
                <input
                    bind:value={job.earnings}
                    class="form-control"
                    id="earnings"
                    type="number"
                />
            </div>
        </div>
        <button type="button" class="btn btn-primary" id="submitbutton" on:click={createJob}
            >Submit</button
        >
    </form>
{/if}

<h1>All Jobs</h1>

<div class="row my-3">
    <div class="col-auto">
        <label for="" class="col-form-label">Earnings: </label>
    </div>
    <div class="col-3">
        <input
            class="form-control"
            type="number"
            placeholder="min"
            id="minEarningFilter"
            bind:value={earningsMin}
        />
        <!--Die Eingaben sind via value-binding mit den Variablen earningsMin und jobType (siehe nächste Seite) verknüpft.
        Für den Apply-Button braucht es keine neue Funktion, wir müssen nur die URL mit den neuen Query-Parametern aktualisieren.
        Dadurch werden via Reactive Statement die Jobs neu geladen. //-->
    </div>
    <div class="col-auto">
        <label for="" class="col-form-label">Job Type: </label>
    </div>
    <div class="col-3">
        <select bind:value={jobType} class="form-select" id="jobTypeFilter" type="text">
            <option value="ALL" />
            <option value="OTHER">OTHER</option>
            <option value="TEST">TEST</option>
            <option value="IMPLEMENT">IMPLEMENT</option>
            <option value="REVIEW">REVIEW</option>
        </select>
    </div>
    <div class="col-3">
        <a
            class="btn btn-primary"
            id="applyButton"
            href={"#/jobs?page=1&jobType=" +
                jobType +
                "&earningsMin=" +
                earningsMin}
            role="button">Apply</a
        >
    </div>
</div>

<table class="table">
    <thead>
        <tr>
            <th scope="col">Description</th>
            <th scope="col">Type</th>
            <th scope="col">Earnings</th>
            <th scope="col">State</th>
            <th scope="col">FreelancerId</th>
            <th scope="col">Actions</th>
        </tr>
    </thead>
    <tbody>
        {#each jobs as job}
            <tr>
                <td>{job.description}</td>
                <td>{job.jobType}</td>
                <td>{job.earnings}</td>
                <td>{job.jobState}</td>
                <td>{job.freelancerId}</td>
                <td>
                    {#if job.jobState === "ASSIGNED"}
                        <span class="badge bg-secondary" id="assignedToMe">Assigned</span>
                    {:else if job.freelancerId === null}
                        <button
                            type="button"
                            class="btn btn-primary btn-sm"
                            id="assignbutton"
                            on:click={() => {
                                assignToMe(job.id);
                            }}
                        >
                            Assign to me
                        </button>
                    {/if}
                    {#if job.freelancerId === $myFreelancerId && job.jobState !== "DONE"}
                        <button
                            type="button"
                            class="btn btn-success btn-sm"
                            on:click={() => {
                                completeJob(job.id);
                            }}
                        >
                            Complete Job
                        </button>
                    {:else if job.jobState === "DONE"}
                        <span class="badge bg-success">Done</span>
                    {/if}
                </td>
                <!-- Wenn der Job den Zustand «ASSIGNED» hat, wird ein Badge mit dem Text «Assigned» angezeigt.
                Wenn dem Job noch kein Freelancer zugewiesen ist (freelancerId ist null), wird der Button «Assign To Me» angezeigt. //-->
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
                    href={"#/jobs?page=" + (i + 1)}
                    >{i + 1}
                </a>
                <!-- Pagination-Element wird blau (active) angezeigt, wenn die aktuelle Page mit dem index (+1) übereinstimmt.
                    
                Bei Klick auf das Pagination-Element soll auf die entsprechende Page gewechselt werden, z.B. http://localhost:8080/#/jobs?page=2//-->
            </li>
        {/each}
    </ul>
</nav>
