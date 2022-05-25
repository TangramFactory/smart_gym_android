package kr.tangram.smartgym.di

import ContributorRepository
import org.koin.dsl.module

val appModule = module {
    factory {
        ContributorRepository(get())
    }
}