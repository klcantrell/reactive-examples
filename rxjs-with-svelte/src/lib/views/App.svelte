<script lang="ts">
  import { swapiData, swapiActions, showLoading } from '../stores/swapiStore';

  import Header from '../components/Header.svelte';
</script>

<main>
  <h1>Star Wars People</h1>
  <button on:click={() => swapiActions.getPeople()}>Fetch them</button>
  {#if $showLoading}
    <p>Loading...</p>
  {:else if $swapiData.status === 'error'}
    <p>Yikes, we ran into some trouble. Try again, please</p>
  {:else if $swapiData.status === 'fetching' || $swapiData.status === 'loaded'}
    <ul>
      {#each $swapiData.data as person (person.name)}
        <li>{person.name}</li>
      {/each}
    </ul>
  {/if}
</main>
<div class="header">
  <Header />
</div>

<style>
  div.header {
    position: absolute;
    top: 16px;
    right: 16px;
  }
  
  ul {
    padding: 0;
  }

  li {
    list-style: none;
  }
</style>
