<script>
    import { isAuthenticated, actualUser } from "../store";
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