package kr.tangram.smartgym.di

import kr.tangram.smartgym.ui.challenge.ChallengeViewModel
import org.koin.dsl.module

val fragmentModule = module {
    ChallengeViewModel()
}