import 'package:flutter/material.dart';
import 'package:rxdart_with_flutter/swapi_store.dart';

class Header extends StatelessWidget {
  Header({super.key});

  final SwapiStore _swapiStore = SwapiStore.shared();

  @override
  Widget build(BuildContext context) {
    return StreamBuilder(
      stream: _swapiStore.peopleData$,
      builder: (context, snapshot) {
        if (snapshot.hasData) {
          final data = snapshot.requireData;
          final status = switch (data) {
            PeopleDataInitial() => 'initial',
            PeopleDataLoading() => 'loading',
            PeopleDataFetching() => 'fetching',
            PeopleDataLoaded() => 'loaded',
            PeopleDataError() => 'error',
          };
          return Text(
            'Status: $status',
            style: const TextStyle(color: Colors.black45),
          );
        } else {
          return const SizedBox.shrink();
        }
      },
    );
  }
}
