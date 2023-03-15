import { Component } from '@angular/core';

import { SwapiService } from '../services/swapi.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  constructor(public swapiService: SwapiService) {}
}
