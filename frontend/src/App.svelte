<script>
	import Router from "svelte-spa-router";
	import routes from "./routes";
	import { isAuthenticated, actualUser } from "./store";
	import auth from "./auth.service";

	const api_root = window.location.origin;
</script>

<div id="app">
	<nav class="navbar navbar-expand-lg">
		<div class="container-fluid">
			<a class="navbar-brand" href={api_root}
				><img src="images/design/logo.png" alt="logo" width="100" /></a
			>

			<div class="collapse navbar-collapse" id="navbarNav">
				<ul class="navbar-nav me-auto mb-2 mb-lg-0">
					{#if $isAuthenticated && $actualUser.user_roles && $actualUser.user_roles.includes("admin")}
						<li class="nav-item">
							<a class="nav-link" href="#/cars"
								>Fahrzeugverwaltung</a
							>
						</li>
						<li class="nav-item">
							<a
								class="nav-link"
								href="https://manage.auth0.com/dashboard/us/dev-qckwt30625gyrsxz/users"
								target="_blank">Benutzerverwaltung</a
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
							<a class="nav-link" href="#/createcars">Vermieten</a
							>
						</li>
					{/if}
					{#if $isAuthenticated && $actualUser.user_roles && !$actualUser.user_roles.includes("admin")}
						<li class="nav-item">
							<a class="nav-link" href="#/overview"
								>Meine Übersicht</a
							>
						</li>
					{/if}
				</ul>
				<div class="d-flex">
					{#if $isAuthenticated && $actualUser.user_roles && !$actualUser.user_roles.includes("admin")}
						<a class="nav-link" href="#/overview">
							{$actualUser.name}
						</a>
					{/if}
					{#if $isAuthenticated && $actualUser.user_roles && $actualUser.user_roles.includes("admin")}
						<!-- svelte-ignore a11y-missing-attribute -->
						<a class="nav-link"> Administrator </a>
					{/if}

					{#if $isAuthenticated}
						<button class="icon-button" on:click={auth.logout}>
							<img
								src="/images/design/logout.png"
								alt="Ausloggen"
								style="margin-left: 5px; width: 30px; height: 30px;"
							/>
						</button>
					{:else}
						<button
							class="icon-button"
							on:click={auth.loginWithPopup}
						>
							<img
								src="/images/design/login.png"
								alt="Einloggen"
								style="margin-left: 5px; width: 30px; height: 30px;"
							/>
						</button>
					{/if}
				</div>
			</div>
		</div>
	</nav>

	<div class="container mt-3">
		<Router {routes} />
	</div>

	<footer class="footer mt-auto py-3">
		<div class="container">
			<div>
				<a href={api_root}
					><img
						src="images/design/logo.png"
						alt="logo"
						width="100"
					/></a
				>
			</div>
			<br />
			<p class="copyright">© 2023 Peer2Vehicle</p>
			<p>
				<a href="mailto:peer2vehicle@outlook.com"
					>support@peer2vehicle.com</a
				>
			</p>
			<div class="social-links">
				<a href="https://facebook.com/">Facebook</a> |
				<a href="https://twitter.com/">Twitter</a> |
				<a href="https://instagram.com/">Instagram</a>
			</div>
			<p class="legal">
				<a href={api_root}>Rechtliche Hinweise</a>
			</p>
		</div>
	</footer>
</div>
