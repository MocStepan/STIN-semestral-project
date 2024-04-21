import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WeatherGraphComponent} from '../../app/weather/weather-graph/weather-graph.component';

describe('WeatherGraphComponent', () => {
  let component: WeatherGraphComponent;
  let fixture: ComponentFixture<WeatherGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WeatherGraphComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(WeatherGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
