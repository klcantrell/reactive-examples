import 'dart:io';

import 'package:flutter/material.dart';
import 'package:rxdart_with_flutter/header.dart';
import 'package:rxdart_with_flutter/swapi_store.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Star Wars People'),
      debugShowCheckedModeBanner: false,
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  late final SwapiStore _store;

  @override
  void initState() {
    super.initState();
    _store = SwapiStore.shared();
  }

  @override
  void dispose() {
    _store.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Stack(
        alignment: Alignment.center,
        children: [
          Positioned.fill(
            child: StreamBuilder(
              stream: _store.peopleData$,
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  final data = snapshot.requireData;
                  return switch (data) {
                    PeopleDataInitial() =>
                      Transform.translate(
                        offset: const Offset(0, -80),
                        child: const Center(
                          child: Text("Press the button below to fetch characters")
                        ),
                      ),
                    PeopleDataLoading() => 
                      StreamBuilder(
                        stream: _store.displayLoader$,
                        builder: (context, snapshot) {
                          if (snapshot.hasData) {
                            final showLoading = snapshot.requireData;
                            if (showLoading) {
                              return Transform.translate(
                                offset: const Offset(0, -80),
                                child: const Padding(
                                  padding: EdgeInsets.only(bottom: 48),
                                  child: Center(child: CircularProgressIndicator()),
                                )
                              );
                            } else {
                              return const SizedBox.shrink();
                            }
                          } else {
                            return const SizedBox.shrink();
                          }
                        },
                      ),
                    PeopleDataLoaded(data: var data) || PeopleDataFetching(data: var data) =>
                      ListView.separated(
                        itemCount: data.length,
                        itemBuilder: (context, index) {
                          return Center(
                            key: ValueKey(data.elementAt(index).name),
                            child: Padding(
                              padding: const EdgeInsets.symmetric(vertical: 8),
                              child: Padding(
                                padding: EdgeInsets.only(top: index == 0 ? 12 : 0),
                                child: Text(
                                  data.elementAt(index).name,
                                  style: const TextStyle(
                                    fontSize: 16,
                                  ),
                                ),
                              ),
                            ),
                          );
                        },
                        separatorBuilder: (context, index) => const Divider(),
                      ),
                    PeopleDataError() =>
                      Transform.translate(
                        offset: const Offset(0, -80),
                        child: const Center(
                          child: Text("Yikes, we ran into some trouble. Try again, please!")
                        ),
                      ),
                    _ => const SizedBox.shrink(),
                  };
                } else {
                  return const SizedBox.shrink();
                }
              }
            ),
          ),
          Positioned(
            top: 0,
            right: 4,
            child: Header(),
          ),
          Positioned(
            bottom: MediaQuery.of(context).padding.bottom + 16,
            left: 16,
            right: 16,
            child: ElevatedButton(
              style: ButtonStyle(
                splashFactory: Platform.isIOS
                  ? NoSplash.splashFactory
                  : InkSplash.splashFactory,
                elevation: MaterialStateProperty.all(8),
              ),
              onPressed: _store.getPeople,
              child: const Padding(
                padding: EdgeInsets.symmetric(vertical: 16.0),
                child: Text(
                  'Fetch them',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
            ),
          )
        ],
      ),
    );
  }
}
