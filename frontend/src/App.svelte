<script>
	import Router from "svelte-spa-router";
	import routes from "./routes";
	import { isAuthenticated, actualUser } from "./store";
	import auth from "./auth.service";
</script>

<div id="app">
	<nav class="navbar navbar-expand-lg bg-light">
		<div class="container-fluid">
			<a class="navbar-brand" href="#/">Peer2Vehicle</a>
			<button
				class="navbar-toggler"
				type="button"
				data-bs-toggle="collapse"
				data-bs-target="#navbarNav"
				aria-controls="navbarNav"
				aria-expanded="false"
				aria-label="Toggle navigation"
			>
				<span class="navbar-toggler-icon" />
			</button>
			<div class="collapse navbar-collapse" id="navbarNav">
				<ul class="navbar-nav me-auto mb-2 mb-lg-0">
					<!--
					{#if $isAuthenticated && $actualUser.user_roles && $actualUser.user_roles.includes("admin")}
						<li class="nav-item">
							<a class="nav-link" href="#/users"
								>Benutzer</a
							>
						</li>
					{/if}
					-->
					{#if $isAuthenticated && $actualUser.user_roles && $actualUser.user_roles.includes("admin")}
						<li class="nav-item">
							<a class="nav-link" href="#/cars"
								>Fahrzeugübersicht</a
							>
						</li>
					{/if}


					{#if $isAuthenticated && $actualUser.user_roles && !$actualUser.user_roles.includes("admin")}
						<li class="nav-item">
							<a class="nav-link" href="#/cars">Mieten</a>
						</li>
					{/if}
					{#if $isAuthenticated && $actualUser.user_roles && !$actualUser.user_roles.includes("admin")}
						<li class="nav-item">
							<a class="nav-link" href="#/createcars">Vermieten</a>
						</li>
					{/if}
					{#if $isAuthenticated && $actualUser.user_roles && !$actualUser.user_roles.includes("admin")}
						<li class="nav-item">
							<a class="nav-link" href="#/overview">Meine Übersicht</a>
						</li>
					{/if}
				</ul>
				<div class="d-flex">
					{#if $isAuthenticated}
						<span class="navbar-text me-2">
							{$actualUser.name}
						</span>
						<button
							type="button"
							class="btn btn-primary"
							on:click={auth.logout}>Ausloggen</button
						>
					{:else}
						<button
							type="button"
							class="btn btn-primary"
							on:click={auth.loginWithPopup}>Einloggen</button
						>
					{/if}
				</div>
			</div>
		</div>
	</nav>

	<div class="container mt-3">
		<Router {routes} />
	</div>
</div>
