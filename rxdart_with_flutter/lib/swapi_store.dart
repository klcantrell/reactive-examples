import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:rxdart/rxdart.dart';
import 'package:http/http.dart' as http;

@immutable
class StarWarsPerson {
  final String name;

  const StarWarsPerson({required this.name});

  StarWarsPerson.fromJson(Map<String, dynamic> json) : name = json['name'];
}

@immutable
class PeopleResponse {
  final Iterable<StarWarsPerson> results;

  const PeopleResponse({required this.results});

  PeopleResponse.fromJson(Map<String, dynamic> json)
      : results = List<Map<String, dynamic>>.from(json['results'])
            .map((personJson) => StarWarsPerson.fromJson(personJson));
}

sealed class PeopleData {}

class PeopleDataInitial extends PeopleData {}

class PeopleDataLoading extends PeopleData {}

class PeopleDataFetching extends PeopleData {
  final Iterable<StarWarsPerson> data;

  PeopleDataFetching({required this.data});
}

class PeopleDataLoaded extends PeopleData {
  final Iterable<StarWarsPerson> data;

  PeopleDataLoaded({required this.data});
}

class PeopleDataError extends PeopleData {}

class SwapiStore {
  Stream<PeopleData> get peopleData$ => _peopleData$.stream;

  final Stream<bool> displayLoader$;

  final Subject<void> _getPeople$;
  final StreamSubscription _getPeopleSubscription$;
  final BehaviorSubject<PeopleData> _peopleData$;

  static final _shared = SwapiStore._internal();

  factory SwapiStore.shared() {
    return _shared;
  }

  factory SwapiStore._internal() {
    var retryCount = 0;

    final getPeople = PublishSubject();
    final peopleData = BehaviorSubject<PeopleData>();
    peopleData.add(PeopleDataInitial());

    final getPeopleSubscription = getPeople
      .debounceTime(const Duration(milliseconds: 300))
      .doOnEach((_) {
        final peopleDataState = peopleData.value;
        switch (peopleDataState) {
          case PeopleDataInitial() || PeopleDataError():
            peopleData.add(PeopleDataLoading());
          case PeopleDataLoaded(data: final data):
            peopleData.add(PeopleDataFetching(data: data));
          default:
        }
      }).switchMap((_) {
        return Rx.retryWhen(
          () => Stream.fromFuture(
            http.get(Uri.https('swapi.dev', '/api/people', {'page': '${1 + Random().nextInt(6)}'})),
          )
              .map(
                  (response) => jsonDecode(response.body) as Map<String, dynamic>)
              .map((data) => PeopleResponse.fromJson(data).results),
          (error, stackTrace) {
            retryCount += 1;
            if (retryCount == 3) {
              retryCount = 0;
              return Stream.error(error, stackTrace);
            } else {
              return Stream.value('retry').delay(
                const Duration(milliseconds: 300),
              );
            }
          },
        )
        .doOnError((error, stackTrace) {
          peopleData.add(PeopleDataError());
        });
      }).listen((data) {
        peopleData.add(PeopleDataLoaded(data: data));
      });

    final displayLoader = peopleData
      .switchMap((data) {
        return switch (data) {
          PeopleDataLoading() =>
            Stream.value(true).delay(const Duration(milliseconds: 500)),
          _ => Stream.value(false),
        };
      });

    return SwapiStore._(
      getPeople$: getPeople,
      getPeopleSubscription$: getPeopleSubscription,
      peopleData$: peopleData,
      displayLoader$: displayLoader,
    );
  }

  void dispose() {
    _getPeopleSubscription$.cancel();
    _getPeople$.close();
    _peopleData$.close();
  }

  void getPeople() {
    _getPeople$.add(null);
  }

  SwapiStore._({
    required Subject<void> getPeople$,
    required getPeopleSubscription$,
    required peopleData$,
    required this.displayLoader$,
  })  : _getPeople$ = getPeople$,
        _getPeopleSubscription$ = getPeopleSubscription$,
        _peopleData$ = peopleData$;
}
