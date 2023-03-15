import { Component } from '@angular/core';

import { SwapiService } from './services/swapi.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(public swapiService: SwapiService) {}
}
