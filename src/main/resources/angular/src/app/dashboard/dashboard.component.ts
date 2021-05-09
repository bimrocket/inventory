import { Component, OnInit } from '@angular/core';
import { UserService } from '../api';
import { DataService } from '../api/api/data.service';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../services/toast.service';
import { CurrentUserService } from '../services/current-user.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  public data;
  public operationsPerDayLabels = [];
  public operationsPerDayDataSet = [];
  public loadingDashboard = true;
  public operationsColors: Array<any> = [
    {
      backgroundColor: 'rgb(245, 209, 66)',
      borderColor: 'rgb(245, 209, 66)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    {
      backgroundColor: 'rgb(127, 62, 135)',
      borderColor: 'rgb(127, 62, 135)',
      pointBackgroundColor: 'rgba(77,83,96,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(77,83,96,1)'
    },
    {
      backgroundColor: 'rgba(120, 120, 120 ,1)',
      borderColor: 'rgba(120, 120, 120 ,1)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    }
  ];
  public operationsStopColors: Array<any> = [
    {
      backgroundColor: 'rgb(245, 209, 66)',
      borderColor: 'rgb(245, 209, 66)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    {
      backgroundColor: 'rgb(255, 115, 0)',
      borderColor: 'rgb(255, 115, 0)',
      pointBackgroundColor: 'rgba(77,83,96,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(77,83,96,1)'
    },
    {
      backgroundColor: 'rgb(110, 49, 0)',
      borderColor: 'rgb(110, 49, 0)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    }
  ];
  public incColors: Array<any> = [
    {
      backgroundColor: 'rgb(245, 209, 66)',
      borderColor: 'rgb(245, 209, 66)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    {
      backgroundColor: 'blue',
      borderColor: 'blue',
      pointBackgroundColor: 'rgba(77,83,96,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(77,83,96,1)'
    },
    {
      backgroundColor: 'green',
      borderColor: 'green',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    {
      backgroundColor: 'red',
      borderColor: 'red',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    }
  ];
  public clientColors: Array<any> = [
    {
      backgroundColor: 'rgba(245, 209, 66, 0.2)',
      borderColor: 'rgb(245, 209, 66)',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    },
    {
      backgroundColor: 'rgba(0,0,255,0.2)',
      borderColor: 'blue',
      pointBackgroundColor: 'rgba(77,83,96,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(77,83,96,1)'
    },
    {
      backgroundColor: 'rgba(0,255,0,0.2)',
      borderColor: 'green',
      pointBackgroundColor: 'rgba(148,159,177,1)',
      pointBorderColor: '#fff',
      pointHoverBackgroundColor: '#fff',
      pointHoverBorderColor: 'rgba(148,159,177,0.8)'
    }
  ];

  
  public readyOperationsPerDay = false;
  public operationsPerHHLabels = [];
  public operationsPerHHDataSet = [];
  public readyOperationsPerHH = false;
  public operStopDayDataset = [];
  public operStopDayLabels = [];
  public readyStopDayChart = false;
  public incDataset = [];
  public incLabels = [];
  public readyIncChart = false;
  public clientDataset = [];
  public clientLabels = [];
  public readyClientChart = false;


  constructor(
    private toast: ToastService, private curUser: CurrentUserService,
    private userService: UserService, private dataService: DataService, private translate: TranslateService) {
  }

  ngOnInit() {
    this.dataService.statisticsData(this.curUser.getMunicipi()).subscribe((data) => {
      this.data = data;
      this.operationsPerDayLabels = data.operationsPerDay.labels;

      this.operationsPerHHLabels = data.operationsPerHour.labels;
      data.operationsPerDay.series.forEach(element => {
        this.operationsPerDayDataSet.push(element)
      });
      this.readyOperationsPerDay = true;

      this.operationsPerHHLabels = data.operationsPerHour.labels;
      data.operationsPerHour.series.forEach(element => {
        this.operationsPerHHDataSet.push(element)
      });
      this.readyOperationsPerHH = true;

      this.incLabels = data.incidents.labels;
      data.incidents.series.forEach(element => {
        this.incDataset.push(element)
      });
      this.readyIncChart = true;

      this.clientLabels = data.accionsAtClient.labels;
      data.accionsAtClient.series.forEach(element => {
        this.clientDataset.push(element)
      });
      this.readyIncChart = true;

      this.readyClientChart = true;
      this.loadingDashboard = false;

    }, (err) => {
      this.toast.error(err);
      this.loadingDashboard = false;
    });


    /* ----------==========     Completed Tasks Chart initialization    ==========---------- 

    const dataCompletedTasksChart: any = {
        labels: ['12p', '3p', '6p', '9p', '12p', '3a', '6a', '9a'],
        series: [
            [230, 750, 450, 300, 280, 240, 200, 190]
        ]
    };

   const optionsCompletedTasksChart: any = {
        lineSmooth: Chartist.Interpolation.cardinal({
            tension: 0
        }),
        low: 0,
        high: 1000, // creative tim: we recommend you to set the high sa the biggest value + something for a better look
        chartPadding: { top: 0, right: 0, bottom: 0, left: 0}
    }

    var completedTasksChart = new Chartist.Line('#completedTasksChart', dataCompletedTasksChart, optionsCompletedTasksChart);

    // start animation for the Completed Tasks Chart - Line Chart
    this.startAnimationForLineChart(completedTasksChart);



    /* ----------==========     Emails Subscription Chart initialization    ==========---------- */


  }

}
